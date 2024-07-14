package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Student;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;

@Log4j2
public class StudentService extends EntityService<Student> {

    public StudentService(List<Student> students, ValidatorService<Student> validator) {
        super(students, validator);
    }

    public Student updateStudent(Student updatedStudent, int id) {
        Student currentStudent = getEntity(student -> student.getId() == id);
        synchronized (entities) {
            log.info("Student {}: validation before update\ncurrent: {}\nupdated:{}", id, currentStudent, updatedStudent);
            if (validateEntity(updatedStudent, validator::validate) && validateEntity(currentStudent, Objects::nonNull)) {
                updatedStudent.setId((long) id);
                entities.set(entities.indexOf(currentStudent), updatedStudent);
                log.info("Student {}: updated", currentStudent.getId());
                return updatedStudent;
            } else {
                return null;
            }
        }
    }

    public boolean addStudent(Student student) {
        synchronized (entities) {
            log.info("Student {}: validation before add\n{}", student.getId(), student);
            if (validateEntity(student, validator::validate, this::isUnique)) {
                entities.add(student);
                    log.info("Student {}: added", student.getId());
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean deleteStudent(int id) {
        synchronized (entities) {
            log.info("Student {}: delete\n{}", id, entities);
            return entities.removeIf(student -> student.getId() == id);
        }
    }
}
