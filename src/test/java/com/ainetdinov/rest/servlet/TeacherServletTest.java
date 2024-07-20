package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.service.BaseTest;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
import com.ainetdinov.rest.service.TeacherService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServletTest extends BaseTest {

    @Mock
    TeacherService teacherService;
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

        TeacherServlet teacherServlet = new TeacherServlet();
        teacherServlet.init(config);

        verify(context, times(1)).getAttribute(WebConstant.TEACHER_SERVICE);
        verify(context, times(1)).getAttribute(WebConstant.HTTP_SERVICE);
        verify(context, times(1)).getAttribute(WebConstant.PARSER_SERVICE);
    }

    @Test
    void doGet_ShouldInvokeGetEntities() throws IOException {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        TeacherServlet servlet = new TeacherServlet();
        servlet.init(servletConfig);

        doReturn(new ConcurrentHashMap<>()).when(teacherService).getEntities();

        servlet.doGet(request, response);
        verify(teacherService, times(1)).getEntities();
    }

    @Test
    void doPut_ShouldSetBadRequest_WhenRequestNotContainPath() throws IOException {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        TeacherServlet servlet = new TeacherServlet();
        servlet.init(servletConfig);

        doReturn(false).when(httpService).containsPath(any(HttpServletRequest.class));

        servlet.doPut(request, response);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    void setupMocks() {
        doReturn(servletContext).when(servletConfig).getServletContext();
        doReturn(httpService).when(servletContext).getAttribute(WebConstant.HTTP_SERVICE);
        doReturn(parsingService).when(servletContext).getAttribute(WebConstant.PARSER_SERVICE);
        doReturn(teacherService).when(servletContext).getAttribute(WebConstant.TEACHER_SERVICE);

        doNothing().when(httpService).prepareResponse(any(HttpServletResponse.class));
    }
}