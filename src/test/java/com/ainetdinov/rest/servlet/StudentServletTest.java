package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.service.BaseTest;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
import com.ainetdinov.rest.service.StudentService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServletTest extends BaseTest {

    @Mock
    StudentService studentService;
    @Mock
    HttpService httpService;
    @Mock
    ParsingService parsingService;
    @Mock
    ServletConfig servletConfig;
    @Mock
    ServletContext servletContext;

    @Test
    void init_ShouldGetServletContextAndSet3Attributes() {
        ServletConfig config = mock(ServletConfig.class);
        ServletContext context = mock(ServletContext.class);
        doReturn(context).when(config).getServletContext();

        StudentServlet servlet = new StudentServlet();
        servlet.init(config);

        verify(context, times(1)).getAttribute(WebConstant.STUDENT_SERVICE);
        verify(context, times(1)).getAttribute(WebConstant.HTTP_SERVICE);
        verify(context, times(1)).getAttribute(WebConstant.PARSER_SERVICE);
    }

    @Test
    void doGet_ShouldEnterFirstBlock_WhenFirstConditionIsTrue() throws IOException {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StudentServlet servlet = new StudentServlet();
        servlet.init(servletConfig);

        doReturn(true).when(httpService).containsPath(any(HttpServletRequest.class));
        doReturn(UUID.randomUUID()).when(httpService).extractUUID(any(HttpServletRequest.class));

        servlet.doGet(request, response);
        verify(httpService, times(1)).containsPath(request);
        verify(httpService, times(1)).extractUUID(request);
        verify(studentService, times(1)).getEntity(any(UUID.class));
        verify(httpService, never()).containsQueryString(request);
    }

    @Test
    void doGet_ShouldEnterSecondBlock_WhenSecondConditionIsTrue() throws IOException {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StudentServlet servlet = new StudentServlet();
        servlet.init(servletConfig);

        doReturn(false).when(httpService).containsPath(any(HttpServletRequest.class));
        doReturn(true).when(httpService).containsQueryString(any(HttpServletRequest.class));

        servlet.doGet(request, response);
        verify(httpService, times(1)).containsPath(request);
        verify(httpService, never()).extractUUID(request);
        verify(httpService, times(1)).containsQueryString(request);
        verify(request, times(1)).getParameter(WebConstant.SURNAME);
    }

    @Test
    void doGet_ShouldEnterThirdBlock_WhenIfConditionsAreFalse() throws IOException {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StudentServlet servlet = new StudentServlet();
        servlet.init(servletConfig);

        doReturn(false).when(httpService).containsPath(any(HttpServletRequest.class));
        doReturn(false).when(httpService).containsQueryString(any(HttpServletRequest.class));
        doReturn(new ConcurrentHashMap<>()).when(studentService).getEntities();

        servlet.doGet(request, response);
        verify(httpService, times(1)).containsPath(request);
        verify(httpService, never()).extractUUID(request);
        verify(httpService, times(1)).containsQueryString(request);
        verify(request, never()).getParameter(WebConstant.SURNAME);
        verify(studentService, times(1)).getEntities();
    }

    @Test
    void doDelete_ShouldSetNoContent_WhenDeleteStudentReturnTrue() {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StudentServlet servlet = new StudentServlet();
        servlet.init(servletConfig);

        doReturn(UUID.randomUUID()).when(httpService).extractUUID(request);
        doReturn(true).when(httpService).containsPath(any(HttpServletRequest.class));
        doReturn(true).when(studentService).deleteStudent(any(UUID.class));

        servlet.doDelete(request, response);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDelete_ShouldSetNotFound_WhenDeleteStudentReturnFalse() {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StudentServlet servlet = new StudentServlet();
        servlet.init(servletConfig);

        doReturn(UUID.randomUUID()).when(httpService).extractUUID(request);
        doReturn(true).when(httpService).containsPath(any(HttpServletRequest.class));
        doReturn(false).when(studentService).deleteStudent(any(UUID.class));

        servlet.doDelete(request, response);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doDelete_ShouldSetBadRequest_WhenRequestNotContainsPath() {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StudentServlet servlet = new StudentServlet();
        servlet.init(servletConfig);

        doReturn(UUID.randomUUID()).when(httpService).extractUUID(request);
        doReturn(false).when(httpService).containsPath(any(HttpServletRequest.class));

        servlet.doDelete(request, response);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPut_ShouldSetBadRequest_WhenRequestNotContainsPath() throws IOException {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StudentServlet servlet = new StudentServlet();
        servlet.init(servletConfig);

        doReturn(UUID.randomUUID()).when(httpService).extractUUID(request);
        doReturn(false).when(httpService).containsPath(any(HttpServletRequest.class));

        servlet.doPut(request, response);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    void setupMocks() {
        doReturn(servletContext).when(servletConfig).getServletContext();
        doReturn(httpService).when(servletContext).getAttribute(WebConstant.HTTP_SERVICE);
        doReturn(parsingService).when(servletContext).getAttribute(WebConstant.PARSER_SERVICE);
        doReturn(studentService).when(servletContext).getAttribute(WebConstant.STUDENT_SERVICE);

        doNothing().when(httpService).prepareResponse(any(HttpServletResponse.class));
    }
}