package com.ainetdinov.rest.listener;

import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.model.Teacher;
import com.ainetdinov.rest.service.*;
import com.ainetdinov.rest.validator.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

@Log4j2
@WebListener
public class AppContextListener implements ServletContextListener {
    private static final String PROPERTIES_PATH = "/WEB-INF/resources/settings.properties";
    private final Properties properties = new Properties();
    private final ParsingService parsingService = new ParsingService();
    private ServletContext context;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();
        initProperties();

        StudentService studentService = new StudentService(initEntities(WebConstant.STUDENTS_PATH, Student.class), new StudentValidator());
        TeacherService teacherService = new TeacherService(initEntities(WebConstant.TEACHERS_PATH, Teacher.class), new TeacherValidator());
        GroupService groupService = new GroupService(initEntities(WebConstant.GROUPS_PATH, Group.class), new GroupValidator(), studentService);
        ScheduleService scheduleService = new ScheduleService(initEntities(WebConstant.SCHEDULES_PATH, Schedule.class), new ScheduleValidator(), teacherService, groupService);

        context.setAttribute(WebConstant.PARSER_SERVICE, parsingService);
        context.setAttribute(WebConstant.STUDENT_SERVICE, studentService);
        context.setAttribute(WebConstant.TEACHER_SERVICE, teacherService);
        context.setAttribute(WebConstant.GROUP_SERVICE, groupService);
        context.setAttribute(WebConstant.SCHEDULE_SERVICE, scheduleService);
        context.setAttribute(WebConstant.HTTP_SERVICE, new HttpService(new HttpValidator()));

        ServletContextListener.super.contextInitialized(sce);
    }

    private void initProperties() {
        try {
            log.debug("Initializing properties file");
            properties.load(context.getResourceAsStream(PROPERTIES_PATH));
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error reading properties file: %s", PROPERTIES_PATH), e);
        }
    }

    private Path getResourcePath(String path) {
        try {
            return Path.of(context.getResource(properties.getProperty(path)).toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> initEntities(String path, Class<T> clazz) {
        log.debug("Initializing {} entities", clazz.getSimpleName());
        return parsingService.parseList(getResourcePath(path), clazz);
    }
}
