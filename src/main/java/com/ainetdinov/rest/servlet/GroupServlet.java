package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.service.GroupService;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

import static com.ainetdinov.rest.constant.Endpoint.*;

@WebServlet(SLASH + GROUPS + SLASH + ASTERISK)
public class GroupServlet extends HttpServlet {
    private GroupService groupService;
    private HttpService httpService;
    private ParsingService parsingService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        groupService = (GroupService) context.getAttribute(WebConstant.GROUP_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstant.HTTP_SERVICE);
        parsingService = (ParsingService) context.getAttribute(WebConstant.PARSER_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        if (httpService.containsQueryString(req)) {
            if (Objects.nonNull(req.getParameter(WebConstant.NUMBER))) {
                getGroupByNumber(req, resp);
            } else {
                getGroupByStudentSurname(req, resp);
            }
        } else {
            resp.getWriter().write(new ArrayList<>(groupService.getEntities().values()).toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        Group group = parsingService.parse(httpService.getRequestBody(req), new TypeReference<>(){});
        if (groupService.addGroup(group)) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        List<Student> students = parsingService.parse(httpService.getRequestBody(req), new TypeReference<>(){});
        Group updatedGroup = groupService.addStudentsToGroup(students, httpService.extractUUID(req));
        resp.getWriter().write(updatedGroup.toString());
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void getGroupByNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String number = req.getParameter(WebConstant.NUMBER);
        Group group = groupService.getEntity(g -> g.getNumber().equals(number));
        httpService.writeResponse(resp, group);
    }

    private void getGroupByStudentSurname(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String studentSurname = req.getParameter(WebConstant.SURNAME);
        Predicate<Group> isStudentPresent = g -> g.getStudents()
                .stream()
                .anyMatch(student -> student.getSurname().equalsIgnoreCase(studentSurname));
        List<Group> groups = groupService.getEntities(isStudentPresent);
        if (groups.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            resp.getWriter().write(groups.toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
