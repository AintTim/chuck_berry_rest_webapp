package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.Subject;
import com.ainetdinov.rest.model.Teacher;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;

@Log4j2
public class TeacherService extends EntityService<Teacher> {

    public TeacherService(List<Teacher> teachers, ValidatorService<Teacher> validator) {
        super(teachers, validator);
    }

    public boolean addTeacher(Teacher teacher) {
        synchronized (entities) {
            log.info("Teacher {}: validation before add\n{}", teacher.getId(), teacher);
            if (validateEntity(teacher, validator::validate, this::isUnique)) {
                entities.add(teacher);
                log.info("Teacher {}: added", teacher.getId());
                return true;
            } else {
                return false;
            }
        }
    }

    public List<Subject> updateTeacherSubjects(List<Subject> subjects, int id) {
        Teacher teacher = getEntity(t -> t.getId() == id);
        synchronized (entities) {
            log.info("Subjects validation before update\n{}", subjects);
            if (!subjects.isEmpty() && Objects.nonNull(teacher)) {
                teacher.setSubjects(subjects);
                log.info("Teacher {}: subjects updated", teacher.getId());
                return teacher.getSubjects();
            } else {
                return null;
            }
        }
    }
}
