package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

@Getter
public class GroupService extends EntityService<Group> {
    private final StudentService studentService;

    public GroupService(List<Group> groups, ValidatorService<Group> validator, StudentService studentService) {
        super(groups, validator);
        this.studentService = studentService;
    }

    @Override
    public List<Group> getEntities() {
        syncGroupStudents();
        return entities;
    }

    @Override
    public Group getEntity(Predicate<Group> filter) {
        syncGroupStudents();
        return super.getEntity(filter);
    }

    public boolean addGroup(Group group) {
        synchronized (entities) {
            if (validateEntity(group, validator::validate, this::isUnique)
                    && validateStudents(group.getStudents(), false)) {
                entities.add(group);
                return true;
            } else {
                return false;
            }
        }
    }

    public void addStudentsToGroup(List<Student> students, int groupId) {
        Group group = getEntity(g -> g.getId() == groupId);
        synchronized (entities) {
            if (validateStudents(students, false)) {
                group.getStudents().addAll(students);
            }
        }
    }

    public Group getGroupByStudentNameAndSurname(String name, String surname) {
        synchronized (entities) {
            Predicate<Group> isFound = group -> group.getStudents()
                    .stream()
                    .anyMatch(student -> student.getName().equals(name) && student.getSurname().equals(surname));
            return getEntity(isFound);
        }
    }

    private void syncGroupStudents() {
        synchronized (this) {
            entities.forEach(group -> {
                if (!validateStudents(group.getStudents(), true)) {
                    group.getStudents().removeIf(student -> !studentService.getEntities().contains(student));
                }
            });
        }
    }

    private boolean validateStudents(List<Student> students, boolean currentStudents) {
        boolean isPresent = new HashSet<>(studentService.getEntities()).containsAll(students);
        if (currentStudents) {
            return isPresent;
        } else {
            boolean isRelatedToGroup = entities.stream()
                    .flatMap(g -> g.getStudents().stream())
                    .anyMatch(students::contains);
            return isPresent && !isRelatedToGroup;
        }
    }
}
