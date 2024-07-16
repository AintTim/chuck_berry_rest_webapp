package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.ScheduleQuery;
import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.model.Teacher;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
import com.ainetdinov.rest.service.ScheduleService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.ainetdinov.rest.constant.Endpoint.*;

@WebServlet(SLASH + SCHEDULES + SLASH + ASTERISK)
public class ScheduleServlet extends HttpServlet {
    private ScheduleService scheduleService;
    private HttpService httpService;
    private ParsingService parsingService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        scheduleService = (ScheduleService) context.getAttribute(WebConstant.SCHEDULE_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstant.HTTP_SERVICE);
        parsingService = (ParsingService) context.getAttribute(WebConstant.PARSER_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        List<Schedule> schedules;
        if (httpService.containsQueryString(req)) {
            if (Objects.nonNull(req.getParameter(WebConstant.STUDENT_SURNAME))) {
                schedules = getSchedulesByStudent(req);
            } else if (Objects.nonNull(req.getParameter(WebConstant.GROUP_NUMBER))) {
                schedules = getSchedulesByGroup(req);
            } else if (Objects.nonNull(req.getParameter(WebConstant.TEACHER_NAME))) {
                schedules = getSchedulesByTeacher(req);
            } else {
                schedules = getSchedulesByDate(req);
            }
        } else {
            schedules = new ArrayList<>(scheduleService.getEntities().values());
        }
        Predicate<List<Schedule>> isNotEmpty = list -> !list.isEmpty();
        httpService.writeResponse(resp, schedules, isNotEmpty, HttpServletResponse.SC_OK, HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        Schedule schedule = parsingService.parse(httpService.getRequestBody(req), new TypeReference<>(){});
        httpService.writeResponse(resp, schedule, scheduleService::addSchedule, HttpServletResponse.SC_OK, HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        Schedule schedule = parsingService.parse(httpService.getRequestBody(req), new TypeReference<>(){});
        if (Objects.nonNull(schedule)) {
            Schedule updatedSchedule = scheduleService.updateSchedule(schedule, httpService.extractUUID(req));
            httpService.writeResponse(resp, updatedSchedule, Objects::nonNull, HttpServletResponse.SC_OK, HttpServletResponse.SC_NOT_FOUND);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private List<Schedule> getSchedulesBy(Object object, ScheduleQuery model) {
        List<Schedule> schedules = new ArrayList<>();
        if (Objects.nonNull(object)) {
            Predicate<Schedule> filter = switch (model) {
                case GROUP, STUDENT -> schedule -> schedule.getGroup().equals(((Group) object).getUuid());
                case TEACHER -> schedule -> schedule.getTeacher().equals(((Teacher) object).getUuid());
                case SCHEDULE -> schedule -> schedule.getStart().toLocalDate().equals(object);
            };
            schedules = scheduleService.getEntities(filter);
        }
        return schedules;
    }

    private List<Schedule> getSchedulesByDate(HttpServletRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(request.getParameter(WebConstant.SCHEDULE_DATE), formatter);
        return getSchedulesBy(date, ScheduleQuery.SCHEDULE);
    }

    private List<Schedule> getSchedulesByStudent(HttpServletRequest request) {
        String studentSurname = request.getParameter(WebConstant.STUDENT_SURNAME);
        String studentName = request.getParameter(WebConstant.STUDENT_NAME);
        Group group = scheduleService.getGroupService().getGroupByStudentNameAndSurname(studentName, studentSurname);
        return getSchedulesBy(group, ScheduleQuery.STUDENT);
    }

    private List<Schedule> getSchedulesByGroup(HttpServletRequest request) {
        String groupNumber = request.getParameter(WebConstant.GROUP_NUMBER);
        Group group = scheduleService.getGroupService().getEntity(g -> g.getNumber().equals(groupNumber));
        return getSchedulesBy(group, ScheduleQuery.GROUP);
    }

    private List<Schedule> getSchedulesByTeacher(HttpServletRequest request) {
        String teacherName = request.getParameter(WebConstant.TEACHER_NAME);
        Teacher teacher = scheduleService.getTeacherService().getEntity(t -> t.getName().equals(teacherName));
        return getSchedulesBy(teacher, ScheduleQuery.TEACHER);
    }
}
