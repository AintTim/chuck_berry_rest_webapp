package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Student;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest extends BaseTest {
    @Spy
    private ValidatorService<Student> validator;

    @Test
    void addStudent_ShouldReturnTrue_WhenValidStudent() {
        StudentService studentService = spy(new StudentService(List.of(defaultStudent()), validator));
        doReturn(true).when(validator).validate(any(Student.class));
        doReturn(true).when(studentService).isUnique(any(Student.class));
        assertThat(studentService.addStudent(defaultStudent()), Matchers.is(true));
    }

    @Test
    void addStudent_ShouldReturnFalse_WhenAddingNotUniqueStudent() {
        StudentService studentService = spy(new StudentService(List.of(defaultStudent()), validator));
        doReturn(true).when(validator).validate(any(Student.class));
        doReturn(false).when(studentService).isUnique(any(Student.class));
        assertThat(studentService.addStudent(defaultStudent()), Matchers.is(false));
    }

    @Test
    void addStudent_ShouldReturnFalse_WhenAddingInvalidStudent() {
        StudentService studentService = new StudentService(List.of(defaultStudent()), validator);
        assertThat(studentService.addStudent(defaultStudent()), Matchers.is(false));
    }

    @Test
    void deleteStudent_ShouldReturnTrue_WhenStudentRemoved() {
        StudentService studentService = new StudentService(List.of(defaultStudent()), validator);
        UUID uuid = UUID.fromString(defaultStudent().getUuid());
        assertThat(studentService.deleteStudent(uuid), Matchers.is(true));
    }

    @Test
    void deleteStudent_ShouldReturnFalse_WhenStudentNotRemoved() {
        StudentService studentService = new StudentService(List.of(defaultStudent()), validator);
        UUID uuid = UUID.fromString(defaultStudent().getUuid().replace("c", "b"));
        assertThat(studentService.deleteStudent(uuid), Matchers.is(false));
    }

    @Test
    void updateStudent_ShouldReturnStudent_WhenStudentUpdated() {
        StudentService studentService = spy(new StudentService(List.of(defaultStudent()), validator));
        doReturn(true).when(validator).validate(any(Student.class));
        doReturn(true).when(studentService).isUnique(any(Student.class));
        Student student = defaultStudent();
        UUID uuid = UUID.fromString(student.getUuid());
        assertThat(studentService.updateStudent(student, uuid), Matchers.equalTo(student));
    }

    @Test
    void updateStudent_ShouldReturnNull_WhenStudentNotExist() {
        StudentService studentService = spy(new StudentService(List.of(defaultStudent()), validator));
        doReturn(true).when(validator).validate(any(Student.class));
        doReturn(true).when(studentService).isUnique(any(Student.class));
        Student student = defaultStudent();
        UUID uuid = UUID.fromString(student.getUuid().replace("c", "b"));
        assertThat(studentService.updateStudent(student, uuid), Matchers.nullValue(Student.class));
    }

    @Test
    void updateStudent_ShouldReturnNull_WhenStudentFailValidation() {
        StudentService studentService = spy(new StudentService(List.of(defaultStudent()), validator));
        doReturn(false).when(validator).validate(any(Student.class));
        Student student = defaultStudent();
        UUID uuid = UUID.fromString(student.getUuid());
        assertThat(studentService.updateStudent(student, uuid), Matchers.nullValue(Student.class));
    }

    @Test
    void updateStudent_ShouldReturnNull_WhenStudentNotUnique() {
        StudentService studentService = spy(new StudentService(List.of(defaultStudent()), validator));
        doReturn(true).when(validator).validate(any(Student.class));
        doReturn(false).when(studentService).isUnique(any(Student.class));
        Student student = defaultStudent();
        UUID uuid = UUID.fromString(student.getUuid());
        assertThat(studentService.updateStudent(student, uuid), Matchers.nullValue(Student.class));
    }
}