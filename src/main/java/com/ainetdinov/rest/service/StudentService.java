package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Student;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
public class StudentService extends EntityService<Student> {

    public StudentService(List<Student> students, ValidatorService<Student> validator) {
        super(students, validator);
    }

    public Student updateStudent(Student updatedStudent, UUID uuid) {
        Student currentStudent = getEntity(uuid);
        log.info("Student {}: validation before update\ncurrent: {}\nupdated:{}", uuid.toString(), currentStudent, updatedStudent);
        if (validateEntity(updatedStudent, validator::validate, this::isUnique) && validateEntity(currentStudent, Objects::nonNull)) {
            updatedStudent.setUuid(uuid.toString());
            entities.put(uuid, updatedStudent);
            log.info("Student {}: updated", uuid);
            return updatedStudent;
        } else {
            return null;
        }
    }

    public boolean addStudent(Student student) {
        log.info("New student: validation before add\n{}", student);
        if (validateEntity(student, validator::validate, this::isUnique)) {
            student.setUuid(generateUUID());
            entities.put(UUID.fromString(student.getUuid()), student);
            log.info("Student {}: added", student.getUuid());
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteStudent(UUID uuid) {
        log.info("Student {}: delete\n{}", uuid, entities);
        return Objects.nonNull(entities.remove(uuid));
    }
}
