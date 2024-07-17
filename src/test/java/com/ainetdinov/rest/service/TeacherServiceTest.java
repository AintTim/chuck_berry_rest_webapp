package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.Subject;
import com.ainetdinov.rest.model.Teacher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest extends BaseTest {
    @Spy
    private ValidatorService<Teacher> validator;

    @Test
    void addTeacher_ShouldReturnTrue_WhenValidTeacher() {
        TeacherService teacherService = spy(new TeacherService(List.of(defaultTeacher()), validator));
        doReturn(true).when(validator).validate(any(Teacher.class));
        doReturn(true).when(teacherService).isUnique(any(Teacher.class));
        assertThat(teacherService.addTeacher(defaultTeacher()), Matchers.is(true));
    }

    @Test
    void addTeacher_ShouldReturnFalse_WhenAddingNotUniqueStudent() {
        TeacherService teacherService = spy(new TeacherService(List.of(defaultTeacher()), validator));
        doReturn(true).when(validator).validate(any(Teacher.class));
        doReturn(false).when(teacherService).isUnique(any(Teacher.class));
        assertThat(teacherService.addTeacher(defaultTeacher()), Matchers.is(false));
    }

    @Test
    void addTeacher_ShouldReturnFalse_WhenAddingInvalidStudent() {
        TeacherService teacherService = spy(new TeacherService(List.of(defaultTeacher()), validator));
        doReturn(false).when(validator).validate(any(Teacher.class));
        assertThat(teacherService.addTeacher(defaultTeacher()), Matchers.is(false));
    }

    @Test
    void updateTeacherSubjects_ShouldReturnUpdatedSubjects_WhenValidData() {
        Teacher teacher = defaultTeacher();
        TeacherService teacherService = spy(new TeacherService(List.of(teacher), validator));
        List<Subject> subjects = teacherService.updateTeacherSubjects(teacher.getSubjects(), UUID.fromString(teacher.getUuid()));
        assertThat(subjects, Matchers.equalTo(teacher.getSubjects()));
    }

    @Test
    void updateTeacherSubjects_ShouldReturnNull_WhenTeacherNotFound() {
        Teacher teacher = defaultTeacher();
        TeacherService teacherService = spy(new TeacherService(List.of(teacher), validator));
        List<Subject> mockSubjects = mock(List.class);
        doReturn(null).when(teacherService).getEntity(any(UUID.class));
        doReturn(false).when(mockSubjects).isEmpty();

        assertThat(teacherService.updateTeacherSubjects(mockSubjects, UUID.randomUUID()), Matchers.nullValue(List.class));
    }

    @Test
    void updateTeacherSubjects_ShouldReturnNull_WhenEmptySubjectList() {
        Teacher teacher = defaultTeacher();
        TeacherService teacherService = spy(new TeacherService(List.of(teacher), validator));
        doReturn(teacher).when(teacherService).getEntity(any(UUID.class));

        assertThat(teacherService.updateTeacherSubjects(new ArrayList<>(), UUID.randomUUID()), Matchers.nullValue(List.class));
    }
}