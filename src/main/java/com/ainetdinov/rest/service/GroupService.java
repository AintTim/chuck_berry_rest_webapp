package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

@Log4j2
@Getter
public class GroupService extends EntityService<Group> {
    private final StudentService studentService;

    public GroupService(List<Group> groups, ValidatorService<Group> validator, StudentService studentService) {
        super(groups, validator);
        this.studentService = studentService;
    }

    @Override
    public ConcurrentMap<UUID, Group> getEntities() {
        syncGroupStudents();
        return entities;
    }

    @Override
    public Group getEntity(UUID uuid) {
        syncGroupStudents();
        return super.getEntity(uuid);
    }

    @Override
    public Group getEntity(Predicate<Group> filter) {
        syncGroupStudents();
        return super.getEntity(filter);
    }

    @Override
    public List<Group> getEntities(Predicate<Group> filter) {
        syncGroupStudents();
        return super.getEntities(filter);
    }

    public boolean addGroup(Group group) {
        group.setUuid(generateUUID());
        log.info("New group {}: validation before add\n{}", group.getNumber(), group);
        if (validateEntity(group, validator::validate, this::isUnique)
                && validateStudents(group.getStudents(), false)) {
            entities.put(UUID.fromString(group.getUuid()), group);
            log.info("Group {}: added", group.getUuid());
            return true;
        } else {
            return false;
        }
    }

    public Group addStudentsToGroup(List<Student> students, UUID uuid) {
        Group group;
        log.info("Looking for group with group uuid {}", uuid);
        group = getEntity(uuid);
        if (Objects.nonNull(group) && validateStudents(students, false)) {
            group.getStudents().addAll(students);
            log.info("Students have been added to group {}\n{}", group.getNumber(), group.getStudents());
        }
        return group;
    }

    public Group getGroupByStudentNameAndSurname(String name, String surname) {
        Predicate<Group> isFound = group -> group.getStudents()
                .stream()
                .anyMatch(student -> student.getName().equals(name) && student.getSurname().equals(surname));
        log.debug("Looking for student {} {}", name, surname);
        return getEntity(isFound);
    }

    private void syncGroupStudents() {
        entities.values().forEach(group -> {
            if (!validateStudents(group.getStudents(), true)) {
                log.debug("Group {}: outdated data", group.getNumber());
                group.getStudents().removeIf(student -> !studentService.getEntities().containsKey(UUID.fromString(student.getUuid())));
            }
        });
    }

    private boolean validateStudents(List<Student> students, boolean currentStudents) {
        boolean isPresent = new HashSet<>(studentService.getEntities().values()).containsAll(students);
        if (currentStudents) {
            log.info("Checking students presence within database");
            return isPresent;
        } else {
            boolean isRelatedToGroup = entities.values()
                    .stream()
                    .flatMap(g -> g.getStudents().stream())
                    .anyMatch(students::contains);
            log.info("Checking students presence within database and existing groups relation absence");
            return isPresent && !isRelatedToGroup;
        }
    }
}
