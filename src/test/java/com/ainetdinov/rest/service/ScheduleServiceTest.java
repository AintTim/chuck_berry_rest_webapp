package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.model.Teacher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest extends BaseServiceTest{

    @Spy
    private ValidatorService<Schedule> validator;
    private final TeacherService teacherService = createTeacherService();
    private final GroupService groupService = createGroupService();

    @Test
    void updateSchedule() {
    }

    @Test
    void addSchedule_ShouldReturnTrue_WhenValidData() {
        Schedule schedule = defaultSchedule();
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(true).when(validator).validate(any(Schedule.class));
        doReturn(true).when(scheduleService).isUnique(any(Schedule.class));
        assertThat(scheduleService.addSchedule(schedule), Matchers.is(true));
    }

    @Test
    void addSchedule_ShouldReturnFalse_WhenInvalidSchedule() {
        Schedule schedule = defaultSchedule();
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(true).when(validator).validate(any(Schedule.class));
        assertThat(scheduleService.addSchedule(schedule), Matchers.is(false));
    }

    @Test
    void addSchedule_ShouldReturnFalse_WhenInvalidNotUniqueSchedule() {
        Schedule schedule = defaultSchedule();
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(true).when(validator).validate(any(Schedule.class));
        doReturn(false).when(scheduleService).isUnique(any(Schedule.class));
        assertThat(scheduleService.addSchedule(schedule), Matchers.is(false));
    }

    @Test
    void addSchedule_ShouldReturnFalse_WhenTeacherNotExist() {
        Schedule schedule = defaultSchedule();
        schedule.setTeacher(schedule.getGroup());
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(true).when(validator).validate(any(Schedule.class));
        doReturn(true).when(scheduleService).isUnique(any(Schedule.class));
        assertThat(scheduleService.addSchedule(schedule), Matchers.is(false));
    }

    @Test
    void addSchedule_ShouldReturnFalse_WhenGroupNotExist() {
        Schedule schedule = defaultSchedule();
        schedule.setGroup(schedule.getTeacher());
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(true).when(validator).validate(any(Schedule.class));
        doReturn(true).when(scheduleService).isUnique(any(Schedule.class));
        assertThat(scheduleService.addSchedule(schedule), Matchers.is(false));
    }

    @Test
    void updateSchedule_ShouldReturnUpdatedSchedule_WhenValidData() {
        Schedule schedule = defaultSchedule();
        schedule.setEnd(LocalDateTime.now().plusDays(1L));
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(true).when(validator).validate(any(Schedule.class));
        doReturn(true).when(scheduleService).isUnique(any(Schedule.class));
        assertThat(scheduleService.updateSchedule(schedule, UUID.fromString(schedule.getUuid())), Matchers.equalTo(schedule));
    }

    @Test
    void updateSchedule_ShouldReturnNull_WhenOriginScheduleNotExist() {
        Schedule schedule = defaultSchedule();
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(null).when(scheduleService).getEntity(any(UUID.class));
        assertThat(scheduleService.updateSchedule(schedule, UUID.fromString(schedule.getUuid())), Matchers.nullValue(Schedule.class));
    }

    @Test
    void updateSchedule_ShouldReturnNull_WhenInvalidSchedule() {
        Schedule schedule = defaultSchedule();
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(false).when(validator).validate(any(Schedule.class));
        assertThat(scheduleService.updateSchedule(schedule, UUID.fromString(schedule.getUuid())), Matchers.nullValue(Schedule.class));
    }

    @Test
    void updateSchedule_ShouldReturnNull_WhenNotUniqueSchedule() {
        Schedule schedule = defaultSchedule();
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(true).when(validator).validate(any(Schedule.class));
        assertThat(scheduleService.updateSchedule(schedule, UUID.fromString(schedule.getUuid())), Matchers.nullValue(Schedule.class));
    }

    @Test
    void updateSchedule_ShouldReturnNull_WhenScheduleGroupNotExist() {
        Schedule schedule = defaultSchedule();
        schedule.setGroup(schedule.getTeacher());
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(true).when(validator).validate(any(Schedule.class));
        doReturn(true).when(scheduleService).isUnique(any(Schedule.class));
        assertThat(scheduleService.updateSchedule(schedule, UUID.fromString(schedule.getUuid())), Matchers.nullValue(Schedule.class));
    }

    @Test
    void updateSchedule_ShouldReturnNull_WhenScheduleTeacherNotExist() {
        Schedule schedule = defaultSchedule();
        schedule.setTeacher(schedule.getGroup());
        ScheduleService scheduleService = spy(new ScheduleService(List.of(schedule), validator, teacherService, groupService));
        doReturn(true).when(validator).validate(any(Schedule.class));
        doReturn(true).when(scheduleService).isUnique(any(Schedule.class));
        assertThat(scheduleService.updateSchedule(schedule, UUID.fromString(schedule.getUuid())), Matchers.nullValue(Schedule.class));
    }

    @Test
    void getTeacherService_ShouldReturnCorrectService() {
        ScheduleService scheduleService = spy(new ScheduleService(List.of(defaultSchedule()), validator, teacherService, groupService));
        assertThat(scheduleService.getTeacherService(), Matchers.equalTo(teacherService));
    }

    @Test
    void getGroupService_shouldReturnCorrectService() {
        ScheduleService scheduleService = spy(new ScheduleService(List.of(defaultSchedule()), validator, teacherService, groupService));
        assertThat(scheduleService.getGroupService(), Matchers.equalTo(groupService));
    }

    private TeacherService createTeacherService() {
        TeacherService mock = mock(TeacherService.class);
        Teacher teacher = defaultTeacher();
        ConcurrentMap<UUID, Teacher> teachers = new ConcurrentHashMap<>(Map.of(
                UUID.fromString(teacher.getUuid()), teacher
        ));
        when(mock.getEntities()).thenReturn(teachers);
        return mock;
    }

    private GroupService createGroupService() {
        GroupService mock = mock(GroupService.class);
        Group group = defaultGroup();
        ConcurrentMap<UUID, Group> groups = new ConcurrentHashMap<>(Map.of(
                UUID.fromString(group.getUuid()), group
        ));
        when(mock.getEntities()).thenReturn(groups);
        return mock;
    }
}