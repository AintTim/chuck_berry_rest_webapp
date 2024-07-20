package com.ainetdinov.rest.constant;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class SubjectTest {

    @Test
    void getSubject_ShouldReturnExpectedSubject() {
        assertThat(Subject.getSubject("math"), Matchers.equalTo(Subject.MATH));
    }

    @Test
    void getSubject_ShouldReturnUnknown_WhenExpectedNotFound() {
        assertThat(Subject.getSubject("random subject"), Matchers.equalTo(Subject.UNKNOWN));
    }

    @Test
    void getSubject_ShouldReturnNull_WhenNameIsNull() {
        assertThat(Subject.getSubject(null), Matchers.nullValue(Subject.class));
    }

    @Test
    void getSubject_ShouldReturnNull_WhenNameIsEmpty() {
        assertThat(Subject.getSubject(""), Matchers.nullValue(Subject.class));
    }
}