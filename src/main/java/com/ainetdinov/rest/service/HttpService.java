package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.Endpoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
public class HttpService {
    private final String EMPTY_STRING = "";

    public int extractId(HttpServletRequest request) {
        return Integer.parseInt(request.getPathInfo().replace(Endpoint.SLASH, EMPTY_STRING));
    }

    public boolean containsQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        log.debug("Checking query presence: {}", queryString);
        return Objects.nonNull(queryString) && !queryString.isEmpty();
    }

    public boolean containsPath(HttpServletRequest request) {
        String path = request.getPathInfo();
        log.debug("Checking path presence: {}", path);
        return Objects.nonNull(path) && !path.replace(Endpoint.SLASH, EMPTY_STRING).isEmpty();
    }

    public void prepareResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
    }

    public String getRequestBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
