package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
import com.ainetdinov.rest.service.StudentService;
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

import static com.ainetdinov.rest.constant.Endpoint.*;

@WebServlet(SLASH + STUDENTS + SLASH + ASTERISK)
public class StudentServlet extends HttpServlet {
    private StudentService studentService;
    private HttpService httpService;
    private ParsingService parsingService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        studentService = (StudentService) context.getAttribute(WebConstant.STUDENT_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstant.HTTP_SERVICE);
        parsingService = (ParsingService) context.getAttribute(WebConstant.PARSER_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        if (httpService.containsPath(req)) {
            getStudentById(httpService.extractUUID(req), resp);
        } else if (httpService.containsQueryString(req)) {
            getStudentsBySurname(req, resp);
        } else {
            resp.getWriter().write(new ArrayList<>(studentService.getEntities().values()).toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        Student student = parsingService.parse(httpService.getRequestBody(req), new TypeReference<>(){});
        if (studentService.addStudent(student)) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        httpService.prepareResponse(resp);
        UUID uuid = httpService.extractUUID(req);
        if (httpService.containsPath(req) && Objects.nonNull(uuid)) {
            if (studentService.deleteStudent(uuid)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        UUID uuid = httpService.extractUUID(req);
        if (httpService.containsPath(req)) {
            Student student = parsingService.parse(httpService.getRequestBody(req), new TypeReference<>(){});
            Student updatedStudent = studentService.updateStudent(student, uuid);
            httpService.writeResponse(resp, updatedStudent);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void getStudentById(UUID uuid, HttpServletResponse response) throws IOException {
        Student student = studentService.getEntity(uuid);
        httpService.writeResponse(response, student);
    }

    private void getStudentsBySurname(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String surname = request.getParameter(WebConstant.SURNAME);
        List<Student> students = studentService.getEntities(s -> s.getSurname().equals(surname));
        if (students.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.getWriter().write(students.toString());
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
