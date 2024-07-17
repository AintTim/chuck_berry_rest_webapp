package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.validator.StudentValidator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StudentServiceTest {
    private static ValidatorService<Student> validator;

    @BeforeAll
    static void setUp() {
        validator = spy(new StudentValidator());
    }

    @Test
    void addStudent_ShouldReturnTrue_WhenValidStudent() {
        StudentService studentService = spy(new StudentService(List.of(defaultStudent()), validator));
        when(studentService.isUnique(any(Student.class))).thenReturn(true);
        assertThat(studentService.addStudent(defaultStudent()), Matchers.is(true));
    }

    @Test
    void addStudent_ShouldReturnFalse_WhenAddingExistingStudent() {
        StudentService studentService = new StudentService(List.of(defaultStudent()), validator);
        assertThat(studentService.addStudent(defaultStudent()), Matchers.is(false));
    }

    @Test
    void addStudent_ShouldReturnFalse_WhenAddingInvalidStudent() {
        StudentService studentService = new StudentService(List.of(defaultStudent()), new StudentValidator());
        assertThat(studentService.addStudent(null), Matchers.is(false));
    }

    @Test
    void deleteStudent_ShouldReturnTrue_WhenStudentRemoved() {
        StudentService studentService = new StudentService(List.of(defaultStudent()), new StudentValidator());
        UUID uuid = UUID.fromString(defaultStudent().getUuid());
        assertThat(studentService.deleteStudent(uuid), Matchers.is(true));
    }

    @Test
    void deleteStudent_ShouldReturnFalse_WhenStudentNotRemoved() {
        StudentService studentService = new StudentService(List.of(defaultStudent()), new StudentValidator());
        UUID uuid = UUID.fromString(defaultStudent().getUuid().replace("c", "b"));
        assertThat(studentService.deleteStudent(uuid), Matchers.is(false));
    }

    @Test
    void updateStudent_ShouldReturnStudent_WhenStudentUpdated() {
        StudentService studentService = new StudentService(List.of(defaultStudent()), new StudentValidator());
        UUID uuid = UUID.fromString(defaultStudent().getUuid());
        Student updated = defaultStudent().toBuilder().name("Jane").build();
        assertThat(studentService.updateStudent(updated, uuid), Matchers.equalTo(updated));
    }

    @Test
    void updateStudent_ShouldReturnNull_WhenStudentNotExist() {
        StudentService studentService = new StudentService(List.of(defaultStudent()), new StudentValidator());
        UUID uuid = UUID.fromString(defaultStudent().getUuid().replace("c", "b"));
        Student updated = defaultStudent().toBuilder().name("Jane").build();
        assertThat(studentService.updateStudent(updated, uuid), Matchers.nullValue(Student.class));
    }

    @Test
    void updateStudent_ShouldReturnNull_WhenStudentFailValidation() {
        StudentService studentService = new StudentService(List.of(defaultStudent()), new StudentValidator());
        UUID uuid = UUID.fromString(defaultStudent().getUuid());
        assertThat(studentService.updateStudent(null, uuid), Matchers.nullValue(Student.class));
    }


    private static Student defaultStudent() {
        return Student.builder()
                .uuid("30ef0869-d5fb-49c7-9b66-16c6bde79d9a")
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.now())
                .phoneNumber("+7 909 192-21-57")
                .build();
    }
}