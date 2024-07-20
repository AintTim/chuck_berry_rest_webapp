package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.Subject;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.model.Teacher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BaseTest {

    protected Student defaultStudent() {
        return Student.builder()
                .uuid("30ef0869-d5fb-49c7-9b66-16c6bde79d9a")
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.now())
                .phoneNumber("+7 909 192-21-57")
                .build();
    }

    protected Group defaultGroup() {
        return Group.builder()
                .uuid("cd8cdd4b-c2a9-4493-bdbe-8e84d38e6ffe")
                .number("1")
                .students(new ArrayList<>(List.of(defaultStudent())))
                .build();
    }

    protected Teacher defaultTeacher() {
        return Teacher.builder()
                .uuid("30ef0869-d5fb-49c7-9b66-16c6bde79d9a")
                .name("Anton")
                .experience(3)
                .subjects(List.of(Subject.MATH))
                .build();
    }

    protected Schedule defaultSchedule() {
        return Schedule.builder()
                .uuid("30ef0869-d5fb-49c7-9b66-16c6bde79d8c")
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .group("cd8cdd4b-c2a9-4493-bdbe-8e84d38e6ffe")
                .teacher("30ef0869-d5fb-49c7-9b66-16c6bde79d9a")
                .build();
    }
}
