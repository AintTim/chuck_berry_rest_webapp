package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.Subject;
import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.model.Teacher;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
import com.ainetdinov.rest.service.TeacherService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ainetdinov.rest.constant.Endpoint.*;

@WebServlet(SLASH + TEACHERS + SLASH + ASTERISK)
public class TeacherServlet extends HttpServlet {
    private TeacherService teacherService;
    private HttpService httpService;
    private ParsingService parsingService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        teacherService = (TeacherService) context.getAttribute(WebConstant.TEACHER_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstant.HTTP_SERVICE);
        parsingService = (ParsingService) context.getAttribute(WebConstant.PARSER_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        httpService.writeResponse(resp, new ArrayList<>(teacherService.getEntities().values()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        Teacher teacher = parsingService.parse(httpService.getRequestBody(req), new TypeReference<>(){});
        httpService.writeResponse(resp, teacher, teacherService::addTeacher, HttpServletResponse.SC_OK, HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        if (httpService.containsPath(req)) {
            List<Subject> subjects = teacherService.updateTeacherSubjects(parseTeacherSubjects(req), httpService.extractUUID(req));
            httpService.writeResponse(resp, subjects, Objects::nonNull, HttpServletResponse.SC_OK, HttpServletResponse.SC_BAD_REQUEST);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private List<Subject> parseTeacherSubjects(HttpServletRequest request) {
        return Arrays.stream(request.getParameterValues(WebConstant.SUBJECTS))
                .map(Subject::getSubject)
                .collect(Collectors.toList());
    }
}
