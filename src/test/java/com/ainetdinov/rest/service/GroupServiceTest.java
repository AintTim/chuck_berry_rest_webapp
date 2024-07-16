package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    private static ValidatorService<Group> validator;
    private static StudentService studentService;

    @BeforeAll
    static void setup() {
        validator = createValidator();
        studentService = createStudentService();
    }
    @Test
    void getEntities_shouldReturnMapOfGroupsWithUuid() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        Map<UUID, Group> entities = new HashMap<>(Map.of(UUID.fromString(group.getUuid()), group));
        assertThat(groupService.getEntities(), Matchers.equalTo(entities));
    }

    @Test
    void getEntity_shouldReturnValidGroupByUuid() {
        Group group = defaultGroup();
        UUID uuid = UUID.fromString(group.getUuid());
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        assertThat(groupService.getEntity(uuid), Matchers.equalTo(group));
    }

    @Test
    void getEntity_shouldReturnValidGroupByPredicate() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        Predicate<Group> condition = g-> g.getNumber().equals("1");
        assertThat(groupService.getEntity(condition), Matchers.equalTo(group));
    }

    @Test
    void getEntities_shouldReturnValidGroupsByPredicate() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        Predicate<Group> condition = g-> g.getNumber().equals("1");
        assertThat(groupService.getEntities(condition), Matchers.equalTo(List.of(group)));
    }

    @Test
    void addGroup_shouldReturnTrue_WhenValidGroup() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        Student student = defaultStudent().toBuilder().name("Jane").build();
        Group additionalGroup = group.toBuilder().number("2").students(List.of(student)).build();
        assertThat(groupService.addGroup(additionalGroup), Matchers.is(true));
    }

    @Test
    void addGroup_shouldReturnFalse_WhenAddingExistingGroup() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        assertThat(groupService.addGroup(group), Matchers.is(false));
    }

    @Test
    void addGroup_shouldReturnFalse_WhenAddingGroupWithNonRegisteredStudent() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        Student student = defaultStudent().toBuilder().name("Error").build();
        Group additionalGroup = group.toBuilder().number("2").students(List.of(student)).build();
        assertThat(groupService.addGroup(additionalGroup), Matchers.is(false));
    }

    @Test
    void addStudentsToGroup_ShouldReturnNotNullAndUpdateGroup_WhenValidStudents() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        Student student = defaultStudent().toBuilder().name("Jane").build();
        UUID groupUuid = UUID.fromString(group.getUuid());
        Group updatedGroup = groupService.addStudentsToGroup(List.of(student), groupUuid);

        assertThat(updatedGroup, Matchers.notNullValue(Group.class));
        assertThat(updatedGroup.getStudents(), Matchers.hasItem(student));
    }

    @Test
    void addStudentsToGroup_ShouldReturnNull_WhenGroupNotFound() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        Student student = defaultStudent().toBuilder().name("Jane").build();
        UUID groupUuid = UUID.fromString(group.getUuid().replace("c", "d"));
        assertThat(groupService.addStudentsToGroup(List.of(student), groupUuid), Matchers.nullValue(Group.class));
    }

    @Test
    void addStudentsToGroup_ShouldReturnGroupWithoutInvalidStudents() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        Student student = defaultStudent().toBuilder().name("Error").build();
        UUID groupUuid = UUID.fromString(group.getUuid());
        assertThat(groupService.addStudentsToGroup(List.of(student), groupUuid).getStudents(), Matchers.not(Matchers.hasItem(student)));
    }

    @Test
    void getGroupByStudentNameAndSurname_ShouldReturnGroup_WhenGroupWithStudentExist() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        Student student = defaultStudent();
        Group filteredGroup = groupService.getGroupByStudentNameAndSurname(student.getName(), student.getSurname());
        assertThat(filteredGroup, Matchers.equalTo(group));
    }

    @Test
    void getGroupByStudentNameAndSurname_ShouldReturnNull_WhenGroupWithStudentNotExist() {
        Group group = defaultGroup();
        GroupService groupService = new GroupService(List.of(group), validator, studentService);
        Group filteredGroup = groupService.getGroupByStudentNameAndSurname("Error", "Error");
        assertThat(filteredGroup, Matchers.nullValue(Group.class));
    }

    private static ValidatorService<Group> createValidator() {
        ValidatorService<Group> mock = mock(ValidatorService.class);
        when(mock.validate(any(Group.class))).thenReturn(true);
        return mock;
    }

    private static StudentService createStudentService() {
        StudentService mock = mock(StudentService.class);
        Student student1 = defaultStudent();
        Student student2 = student1.toBuilder().name("Jane").uuid("30ef0869-d5fb-49c7-9b66-16c6bde79d9b").build();
        Map<UUID, Student> students = Map.of(
                UUID.fromString(student1.getUuid()), student1,
                UUID.fromString(student2.getUuid()), student2);
        when(mock.getEntities()).thenReturn(students);
        return mock;
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

    private static Group defaultGroup() {
        return Group.builder()
                .uuid("cd8cdd4b-c2a9-4493-bdbe-8e84d38e6ffe")
                .number("1")
                .students(new ArrayList<>(List.of(defaultStudent())))
                .build();
    }
}