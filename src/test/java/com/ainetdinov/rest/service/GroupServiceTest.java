package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest extends BaseTest {
    @Spy
    private ValidatorService<Group> validator;
    private final StudentService studentService = createStudentService();

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
    void addGroup_shouldReturnTrue_WhenValidData() {
        Group group = defaultGroup();
        GroupService groupService = spy(new GroupService(List.of(group), validator, studentService));
        doReturn(true).when(validator).validate(any(Group.class));
        doReturn(true).when(groupService).isUnique(any(Group.class));

        Student student = defaultStudent().toBuilder().name("Jane").build();
        Group additionalGroup = group.toBuilder().number("2").students(List.of(student)).build();

        assertThat(groupService.addGroup(additionalGroup), Matchers.is(true));
    }

    @Test
    void addGroup_shouldReturnFalse_WhenAddingInvalidGroup() {
        Group group = defaultGroup();
        GroupService groupService = spy(new GroupService(List.of(group), validator, studentService));
        doReturn(false).when(validator).validate(any(Group.class));

        assertThat(groupService.addGroup(group), Matchers.is(false));
    }

    @Test
    void addGroup_shouldReturnFalse_WhenAddingNotUniqueGroup() {
        Group group = defaultGroup();
        GroupService groupService = spy(new GroupService(List.of(group), validator, studentService));
        doReturn(true).when(validator).validate(any(Group.class));
        doReturn(false).when(groupService).isUnique(any(Group.class));

        assertThat(groupService.addGroup(group), Matchers.is(false));
    }

    @Test
    void addGroup_shouldReturnFalse_WhenAddingGroupWithNonRegisteredStudent() {
        Group group = defaultGroup();
        GroupService groupService = spy(new GroupService(List.of(group), validator, studentService));
        doReturn(true).when(validator).validate(any(Group.class));
        doReturn(true).when(groupService).isUnique(any(Group.class));

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

    private StudentService createStudentService() {
        StudentService mock = mock(StudentService.class);
        Student student1 = defaultStudent();
        Student student2 = student1.toBuilder().name("Jane").uuid("30ef0869-d5fb-49c7-9b66-16c6bde79d9b").build();
        ConcurrentMap<UUID, Student> students = new ConcurrentHashMap<>(Map.of(
                UUID.fromString(student1.getUuid()), student1,
                UUID.fromString(student2.getUuid()), student2));
        when(mock.getEntities()).thenReturn(students);
        return mock;
    }
}