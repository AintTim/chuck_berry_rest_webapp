package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Student;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

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
            if (validateEntity(updatedStudent, validator::validate) && validateEntity(currentStudent, Objects::nonNull)) {
                updatedStudent.setId((long) id);
                entities.set(entities.indexOf(currentStudent), updatedStudent);
                return updatedStudent;
            } else {
                return null;
            }
        }
    }

    public boolean addStudent(Student student) {
        synchronized (entities) {
            log.info("Entity validation {}", student);
            if (validateEntity(student, validator::validate, this::isUnique)) {
                entities.add(student);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean deleteStudent(int id) {
        synchronized (entities) {
            return entities.removeIf(student -> student.getId() == id);
        }
    }
}
