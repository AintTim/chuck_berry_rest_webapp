package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstant;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class GroupServletTest {

    @Test
    void init_ShouldGetServletContextAndSet4Attributes() {
        ServletConfig config = mock(ServletConfig.class);
        ServletContext context = mock(ServletContext.class);
        doReturn(context).when(config).getServletContext();

        GroupServlet servlet = new GroupServlet();

        servlet.init(config);

        verify(context, times(1)).getAttribute(WebConstant.GROUP_SERVICE);
        verify(context, times(1)).getAttribute(WebConstant.HTTP_SERVICE);
        verify(context, times(1)).getAttribute(WebConstant.PARSER_SERVICE);
        verify(context, times(1)).getAttribute(WebConstant.PRECONDITION_SERVICE);
    }
}