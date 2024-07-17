package com.ainetdinov.rest.validator;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpValidatorTest {
    private HttpValidator validator = new HttpValidator();
    @Test
    void validate_ShouldReturnTrue_WhenValidString() {
        assertThat(validator.validate(UUID.randomUUID().toString()), Matchers.is(true));
    }

    @Test
    void validate_ShouldReturnFalse_WhenInvalidString() {
        assertThat(validator.validate(UUID.randomUUID() + "abc"), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenEmptyString() {
        assertThat(validator.validate(""), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenNull() {
        assertThat(validator.validate(null), Matchers.is(false));
    }
}