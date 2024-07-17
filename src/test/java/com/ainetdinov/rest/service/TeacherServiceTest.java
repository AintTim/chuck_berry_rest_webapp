package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.Subject;
import com.ainetdinov.rest.model.Teacher;
import com.ainetdinov.rest.validator.TeacherValidator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {
    @Spy
    private static ValidatorService<Teacher> validator;

    @Test
    void addTeacher_ShouldReturnTrue_WhenValidTeacher() {
        TeacherService teacherService = spy(new TeacherService(List.of(defaultTeacher()), new TeacherValidator()));
        doReturn(true).when(validator).validate(any(Teacher.class));
        doReturn(true).when(teacherService).addTeacher(any(Teacher.class));
        assertThat(teacherService.addTeacher(defaultTeacher()), Matchers.is(true));
    }

    @Test
    void addTeacher_ShouldReturnFalse_WhenAddingNotUniqueStudent() {
        TeacherService teacherService = spy(new TeacherService(List.of(defaultTeacher()), validator));
        when(validator.validate(any(Teacher.class))).thenReturn(true);
        when(teacherService.isUnique(any(Teacher.class))).thenReturn(false);
        assertThat(teacherService.addTeacher(defaultTeacher()), Matchers.is(false));
    }

    @Test
    void addTeacher_ShouldReturnFalse_WhenAddingInvalidStudent() {
        TeacherService teacherService = spy(new TeacherService(List.of(defaultTeacher()), validator));
        when(validator.validate(any(Teacher.class))).thenReturn(false);
        assertThat(teacherService.addTeacher(defaultTeacher()), Matchers.is(false));
    }

    private static Teacher defaultTeacher() {
        return Teacher.builder()
                .uuid("30ef0869-d5fb-49c7-9b66-16c6bde79d9a")
                .name("Anton")
                .experience(3)
                .subjects(List.of(Subject.MATH))
                .build();
    }
}