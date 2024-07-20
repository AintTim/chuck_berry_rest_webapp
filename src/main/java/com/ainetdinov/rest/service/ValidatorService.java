package com.ainetdinov.rest.service;

@FunctionalInterface
public interface ValidatorService<T> {
    boolean validate(T object);
}
