package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.Endpoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log4j2
public class HttpService {
    private final ValidatorService<String> validator;
    private final String EMPTY_STRING = "";

    public HttpService(ValidatorService<String> validator) {
        this.validator = validator;
    }

    public UUID extractUUID(HttpServletRequest request) {
        String uuid = request.getPathInfo().replace(Endpoint.SLASH, EMPTY_STRING);
        log.info("Validating UUID: {}", uuid);
        if (validator.validate(uuid)) {
            return UUID.fromString(uuid);
        } else {
            log.warn("Invalid UUID: {}", uuid);
            return null;
        }
    }

    public void writeResponse(HttpServletResponse response, Object object, int invalidStatus) throws IOException {
        if (Objects.nonNull(object)) {
            response.getWriter().write(object.toString());
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(invalidStatus);
        }
    }

    public <T> void writeResponse(HttpServletResponse response, T object, Predicate<T> condition, int validStatus, int invalidStatus) throws IOException {
        if (condition.test(object)) {
            response.getWriter().write(object.toString());
            response.setStatus(validStatus);
        } else {
            response.setStatus(invalidStatus);
        }
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
