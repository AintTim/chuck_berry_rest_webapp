package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.Subject;
import com.ainetdinov.rest.model.Teacher;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
public class TeacherService extends EntityService<Teacher> {

    public TeacherService(List<Teacher> teachers, ValidatorService<Teacher> validator) {
        super(teachers, validator);
    }

    public boolean addTeacher(Teacher teacher) {
        log.info("New teacher: validation before add\n{}", teacher);
        if (validateEntity(teacher, validator::validate, this::isUnique)) {
            teacher.setUuid(generateUUID());
            entities.put(UUID.fromString(teacher.getUuid()), teacher);
            log.info("Teacher {}: added", teacher.getUuid());
            return true;
        } else {
            return false;
        }
    }

    public List<Subject> updateTeacherSubjects(List<Subject> subjects, UUID uuid) {
        Teacher teacher = getEntity(uuid);
        log.info("Subjects validation before update\n{}", subjects);
        if (!subjects.isEmpty() && Objects.nonNull(teacher)) {
            teacher.setSubjects(subjects);
            log.info("Teacher {}: subjects updated", teacher.getUuid());
            return teacher.getSubjects();
        } else {
            return null;
        }
    }
}
