package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.model.Student;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("uchecked")
class PreconditionServiceTest extends BaseTest {
    @Mock
    Properties properties;

    @InjectMocks
    PreconditionService preconditionService;

    @Test
    void validateGroupAdd_ShouldReturnTrue_WhenMeetLimit() {
        Group group = defaultGroup();

        doReturn("5").when(properties).getProperty(WebConstant.MAX_GROUP_SIZE);
        doReturn("1").when(properties).getProperty(WebConstant.MIN_GROUP_SIZE);
        assertThat(preconditionService.validateGroupAdd(group), Matchers.is(true));
    }

    @Test
    void validateGroupAdd_ShouldReturnFalse_WhenExceedsLimit() {
        Group group = mock(Group.class);
        List<Student> students = mock(List.class);

        doReturn("5").when(properties).getProperty(WebConstant.MAX_GROUP_SIZE);
        doReturn("1").when(properties).getProperty(WebConstant.MIN_GROUP_SIZE);
        doReturn(students).when(group).getStudents();
        doReturn(6).when(students).size();
        assertThat(preconditionService.validateGroupAdd(group), Matchers.is(false));
    }

    @Test
    void validateGroupAdd_ShouldReturnFalse_WhenZeroStudents() {
        Group group = mock(Group.class);
        List<Student> students = mock(List.class);

        doReturn("5").when(properties).getProperty(WebConstant.MAX_GROUP_SIZE);
        doReturn("1").when(properties).getProperty(WebConstant.MIN_GROUP_SIZE);
        doReturn(students).when(group).getStudents();
        doReturn(0).when(students).size();
        assertThat(preconditionService.validateGroupAdd(group), Matchers.is(false));
    }

    @Test
    void validateGroupUpdate_ShouldReturnTrue_WhenMeetLimit() {
        Group group = defaultGroup();
        List<Student> students = mock(List.class);

        doReturn("5").when(properties).getProperty(WebConstant.MAX_GROUP_SIZE);
        doReturn("1").when(properties).getProperty(WebConstant.MIN_GROUP_SIZE);
        doReturn(1).when(students).size();
        assertThat(preconditionService.validateGroupUpdate(students, group), Matchers.is(true));
    }

    @Test
    void validateGroupUpdate_ShouldReturnFalse_WhenExceedsLimit() {
        Group group = defaultGroup();
        List<Student> students = mock(List.class);

        doReturn("5").when(properties).getProperty(WebConstant.MAX_GROUP_SIZE);
        doReturn("1").when(properties).getProperty(WebConstant.MIN_GROUP_SIZE);
        doReturn(10).when(students).size();
        assertThat(preconditionService.validateGroupUpdate(students, group), Matchers.is(false));
    }

    @Test
    void validateGroupUpdate_ShouldReturnFalse_WhenZeroStudents() {
        Group group = mock(Group.class);
        List<Student> originStudents = mock(List.class);
        List<Student> additionalStudents = mock(List.class);

        doReturn("5").when(properties).getProperty(WebConstant.MAX_GROUP_SIZE);
        doReturn("1").when(properties).getProperty(WebConstant.MIN_GROUP_SIZE);
        doReturn(0).when(originStudents).size();
        doReturn(originStudents).when(group).getStudents();
        doReturn(0).when(additionalStudents).size();

        assertThat(preconditionService.validateGroupUpdate(additionalStudents, group), Matchers.is(false));
    }

    @Test
    void validateGroupSchedule_ShouldReturnTrue_WhenMeetLimit() {
        Schedule schedule = defaultSchedule();
        ScheduleService scheduleService = mock(ScheduleService.class);
        List<Schedule> schedules = mock(List.class);

        doReturn("2").when(properties).getProperty(anyString());
        doReturn(schedules).when(scheduleService).getEntities(any());
        doReturn(1).when(schedules).size();

        assertThat(preconditionService.validateGroupSchedule(schedule, scheduleService), Matchers.is(true));
    }

    @Test
    void validateGroupSchedule_ShouldReturnFalse_WhenExceedsLimit() {
        Schedule schedule = defaultSchedule();
        ScheduleService scheduleService = mock(ScheduleService.class);
        List<Schedule> schedules = mock(List.class);

        doReturn("1").when(properties).getProperty(anyString());
        doReturn(schedules).when(scheduleService).getEntities(any());
        doReturn(1).when(schedules).size();

        assertThat(preconditionService.validateGroupSchedule(schedule, scheduleService), Matchers.is(false));
    }
}