package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.validator.HttpValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpServiceTest extends BaseTest {
    @Mock
    HttpValidator validator;
    @InjectMocks
    HttpService httpService;

    @Test
    void extractUUID_ShouldReturnUuid_WhenValidUuidString() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("30ef0869-d5fb-49c7-9b66-16c6bde79d9a").when(request).getPathInfo();
        doReturn(true).when(validator).validate(anyString());
        assertThat(httpService.extractUUID(request), Matchers.instanceOf(UUID.class));
    }

    @Test
    void extractUUID_ShouldReturnNull_WhenValidInvalidUuid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("30ef0869-d5fb-49c7-9b66-16c6bde79d9a").when(request).getPathInfo();
        doReturn(false).when(validator).validate(anyString());
        assertThat(httpService.extractUUID(request), Matchers.nullValue(UUID.class));
    }

    @Test
    void writeResponse_ShouldWriteObject_WhenObjectNotNull() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);

        doNothing().when(response).setStatus(anyInt());
        doReturn(new PrintWriter("writer")).when(response).getWriter();

        httpService.writeResponse(response, defaultStudent(), HttpServletResponse.SC_NOT_FOUND);
        verify(response, times(1)).getWriter();
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(response, never()).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void writeResponse_ShouldReturnNotFound_WhenObjectIsNull() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);

        doNothing().when(response).setStatus(anyInt());

        httpService.writeResponse(response, null, HttpServletResponse.SC_NOT_FOUND);
        verify(response, never()).getWriter();
        verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void genericWriteResponse_ShouldWriteObjectAndSetValidStatus_WhenObjectNotNull() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        Predicate<Student> condition = mock(Predicate.class);

        doReturn(true).when(condition).test(any(Student.class));
        doNothing().when(response).setStatus(anyInt());
        doReturn(new PrintWriter("writer")).when(response).getWriter();

        httpService.writeResponse(response, defaultStudent(), condition, 1, -1);
        verify(response, times(1)).getWriter();
        verify(response, times(1)).setStatus(1);
    }

    @Test
    void genericWriteResponse_ShouldSetInvalidStatus_WhenObjectIsNull() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        Predicate<Student> condition = mock(Predicate.class);

        doReturn(false).when(condition).test(any(Student.class));
        doNothing().when(response).setStatus(anyInt());

        httpService.writeResponse(response, defaultStudent(), condition, 1, -1);
        verify(response, never()).getWriter();
        verify(response, times(1)).setStatus(-1);
    }

    @Test
    void containsQueryString_ShouldReturnTrue_WhenQueryIsPresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("query").when(request).getQueryString();
        assertThat(httpService.containsQueryString(request), Matchers.is(true));
    }

    @Test
    void containsQueryString_ShouldReturnFalse_WhenQueryIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn(null).when(request).getQueryString();
        assertThat(httpService.containsQueryString(request), Matchers.is(false));
    }

    @Test
    void containsQueryString_ShouldReturnFalse_WhenQueryIsEmpty() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("").when(request).getQueryString();
        assertThat(httpService.containsQueryString(request), Matchers.is(false));
    }

    @Test
    void containsPath_ShouldReturnTrue_WhenPathIsPresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("path").when(request).getPathInfo();
        assertThat(httpService.containsPath(request), Matchers.is(true));
    }

    @Test
    void containsPath_ShouldReturnFalse_WhenPathIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn(null).when(request).getPathInfo();
        assertThat(httpService.containsPath(request), Matchers.is(false));
    }

    @Test
    void containsPath_ShouldReturnFalse_WhenPathIsEmpty() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("").when(request).getPathInfo();
        assertThat(httpService.containsPath(request), Matchers.is(false));
    }

    @Test
    void prepareResponse_ShouldSetData() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        doNothing().when(response).setContentType(anyString());
        doNothing().when(response).setCharacterEncoding(anyString());
        doNothing().when(response).setHeader(anyString(), anyString());

        httpService.prepareResponse(response);
        verify(response, times(1)).setContentType("application/json");
        verify(response, times(1)).setCharacterEncoding("UTF-8");
        verify(response, times(1)).setHeader("Content-Type", "application/json");
    }
}