package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstant;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServletTest {

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
}