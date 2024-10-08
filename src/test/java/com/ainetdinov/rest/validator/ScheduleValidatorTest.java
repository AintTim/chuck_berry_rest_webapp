package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.service.BaseTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class ScheduleValidatorTest extends BaseTest {
    private final ScheduleValidator validator = new ScheduleValidator();

    @Test
    void validate_ShouldReturnTrue_WhenScheduleIsValid() {
        assertThat(validator.validate(defaultSchedule()), Matchers.is(true));
    }

    @Test
    void validate_ShouldReturnTrue_WhenNullUuid() {
        Schedule schedule = defaultSchedule();
        schedule.setUuid(null);
        assertThat(validator.validate(schedule), Matchers.is(true));
    }

    @Test
    void validate_ShouldReturnFalse_WhenScheduleIsNull() {
        assertThat(validator.validate(null), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenStartDateIsNotAfterCurrentDate() {
        Schedule schedule = defaultSchedule();
        schedule.setStart(LocalDateTime.now());
        assertThat(validator.validate(schedule), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenEndDateIsBeforeStartDate() {
        Schedule schedule = defaultSchedule();
        schedule.setEnd(schedule.getStart());
        assertThat(validator.validate(schedule), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenScheduleUuidIsInvalid() {
        Schedule schedule = defaultSchedule();
        schedule.setUuid(schedule.getUuid() + "abc");
        assertThat(validator.validate(schedule), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenGroupUuidIsInvalid() {
        Schedule schedule = defaultSchedule();
        schedule.setGroup(schedule.getGroup() + "abc");
        assertThat(validator.validate(schedule), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenTeacherUuidIsInvalid() {
        Schedule schedule = defaultSchedule();
        schedule.setTeacher(schedule.getTeacher() + "abc");
        assertThat(validator.validate(schedule), Matchers.is(false));
    }
}