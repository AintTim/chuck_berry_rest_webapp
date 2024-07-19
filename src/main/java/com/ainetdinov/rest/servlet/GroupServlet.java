package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.service.GroupService;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
import com.ainetdinov.rest.service.PreconditionService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.ainetdinov.rest.constant.Endpoint.*;

@Log4j2
@WebServlet(SLASH + GROUPS + SLASH + ASTERISK)
public class GroupServlet extends HttpServlet {
    private GroupService groupService;
    private HttpService httpService;
    private ParsingService parsingService;
    private PreconditionService preconditionService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        groupService = (GroupService) context.getAttribute(WebConstant.GROUP_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstant.HTTP_SERVICE);
        parsingService = (ParsingService) context.getAttribute(WebConstant.PARSER_SERVICE);
        preconditionService  = (PreconditionService) context.getAttribute(WebConstant.PRECONDITION_SERVICE);
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
            httpService.writeResponse(resp, new ArrayList<>(groupService.getEntities().values()), HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        Group group = parsingService.parse(httpService.getRequestBody(req), new TypeReference<>(){});
        if (preconditionService.validateGroupAdd(group)) {
            httpService.writeResponse(resp, group, groupService::addGroup, HttpServletResponse.SC_CREATED, HttpServletResponse.SC_BAD_REQUEST);
        } else {
            log.warn("Number of group students {} exceeds the limit", group.getStudents().size());
            httpService.writeResponse(resp, null, HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        List<Student> students = parsingService.parse(httpService.getRequestBody(req), new TypeReference<>(){});
        Group group = groupService.getEntity(httpService.extractUUID(req));
        if (preconditionService.validateGroupUpdate(students, group)) {
            Group updatedGroup = groupService.addStudentsToGroup(students, httpService.extractUUID(req));
            httpService.writeResponse(resp, updatedGroup, HttpServletResponse.SC_NOT_FOUND);
        } else {
            log.warn("Number of updated group students {} exceeds the limit", group.getStudents().size() + students.size());
            httpService.writeResponse(resp, null, HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void getGroupByNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String number = req.getParameter(WebConstant.NUMBER);
        Group group = groupService.getEntity(g -> g.getNumber().equals(number));
        httpService.writeResponse(resp, group, HttpServletResponse.SC_NOT_FOUND);
    }

    private void getGroupByStudentSurname(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String studentSurname = req.getParameter(WebConstant.SURNAME);
        Predicate<Group> isStudentPresent = g -> g.getStudents()
                .stream()
                .anyMatch(student -> student.getSurname().equalsIgnoreCase(studentSurname));
        List<Group> groups = groupService.getEntities(isStudentPresent);
        Predicate<List<Group>> isNotEmpty = list -> !list.isEmpty();
        httpService.writeResponse(resp, groups, isNotEmpty, HttpServletResponse.SC_OK, HttpServletResponse.SC_NOT_FOUND);
    }
}
