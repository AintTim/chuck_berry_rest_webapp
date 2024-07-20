package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.service.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServletTest extends BaseTest {

    @Mock
    GroupService groupService;
    @Mock
    HttpService httpService;
    @Mock
    ParsingService parsingService;
    @Mock
    PreconditionService preconditionService;
    @Mock
    ServletConfig servletConfig;
    @Mock
    ServletContext servletContext;

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

    @Test
    void doGet_ShouldInvokeGetEntities_WhenRequestNotContainPath() throws IOException {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        GroupServlet servlet = new GroupServlet();
        servlet.init(servletConfig);

        doReturn(false).when(httpService).containsQueryString(any(HttpServletRequest.class));
        doReturn(new ConcurrentHashMap<>()).when(groupService).getEntities();

        servlet.doGet(request, response);
        verify(httpService, times(1)).containsQueryString(request);
        verify(request, never()).getParameter(WebConstant.NUMBER);
        verify(request, never()).getParameter(WebConstant.SURNAME);
        verify(groupService, times(1)).getEntities();
    }

    @Test
    void doPost_ShouldNotInvokeAddStudentGroup_WhenPreconditionValidationFail() throws IOException {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        GroupServlet servlet = new GroupServlet();
        servlet.init(servletConfig);
        Group group = defaultGroup();

        doReturn("").when(httpService).getRequestBody(request);
        doReturn(group).when(parsingService).parse(anyString(), any());
        doReturn(false).when(preconditionService).validateGroupAdd(group);

        servlet.doPost(request, response);
        verify(groupService, never()).addGroup(any(Group.class));
    }

    @Test
    void doPut_ShouldInvokeAddStudentsToGroup_WhenPreconditionValidationPass() throws IOException {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        GroupServlet servlet = new GroupServlet();
        servlet.init(servletConfig);
        Group group = defaultGroup();
        UUID uuid = UUID.randomUUID();
        List<Student> students = new ArrayList<>();

        doReturn("").when(httpService).getRequestBody(request);
        doReturn(students).when(parsingService).parse(anyString(), any());
        doReturn(uuid).when(httpService).extractUUID(request);
        doReturn(group).when(groupService).getEntity(any(UUID.class));
        doReturn(true).when(preconditionService).validateGroupUpdate(anyList(), any(Group.class));
        doReturn(group).when(groupService).addStudentsToGroup(students, uuid);

        servlet.doPut(request, response);
        verify(groupService, times(1)).addStudentsToGroup(students, uuid);
    }

    @Test
    void doPut_ShouldNotInvokeAddStudentsToGroup_WhenPreconditionValidationFail() throws IOException {
        setupMocks();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        GroupServlet servlet = new GroupServlet();
        servlet.init(servletConfig);
        Group group = defaultGroup();
        UUID uuid = UUID.randomUUID();
        List<Student> students = new ArrayList<>();

        doReturn("").when(httpService).getRequestBody(request);
        doReturn(students).when(parsingService).parse(anyString(), any());
        doReturn(uuid).when(httpService).extractUUID(request);
        doReturn(group).when(groupService).getEntity(any(UUID.class));
        doReturn(false).when(preconditionService).validateGroupUpdate(anyList(), any(Group.class));

        servlet.doPut(request, response);
        verify(groupService, never()).addStudentsToGroup(anyList(), any(UUID.class));
    }

    void setupMocks() {
        doReturn(servletContext).when(servletConfig).getServletContext();
        doReturn(httpService).when(servletContext).getAttribute(WebConstant.HTTP_SERVICE);
        doReturn(parsingService).when(servletContext).getAttribute(WebConstant.PARSER_SERVICE);
        doReturn(groupService).when(servletContext).getAttribute(WebConstant.GROUP_SERVICE);
        doReturn(preconditionService).when(servletContext).getAttribute(WebConstant.PRECONDITION_SERVICE);

        doNothing().when(httpService).prepareResponse(any(HttpServletResponse.class));
    }
}