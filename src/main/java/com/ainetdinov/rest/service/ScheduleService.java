package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.model.Teacher;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class ScheduleService extends EntityService<Schedule> {
    private final TeacherService teacherService;
    private final GroupService groupService;

    public ScheduleService(List<Schedule> schedules, ValidatorService<Schedule> validator, TeacherService teacherService, GroupService groupService) {
        super(schedules, validator);
        this.teacherService = teacherService;
        this.groupService = groupService;
    }

    public Schedule updateSchedule(Schedule current, Schedule updated) {
        synchronized (entities) {
            if (validateScheduleUpdate(current, updated)) {
                entities.set(entities.indexOf(current), updated);
                return updated;
            } else {
                return null;
            }
        }
    }

    public boolean addSchedule(Schedule schedule) {
        synchronized (this) {
            if (validateEntity(schedule, validator::validate, this::isUnique, this::validateTeacherAndGroupPresence)) {
                entities.add(schedule);
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean validateScheduleUpdate(Schedule current, Schedule updated) {
        return validateEntity(current, validator::validate, entities::contains, this::validateTeacherAndGroupPresence)
                && validateEntity(updated, validator::validate, this::isUnique, this::validateTeacherAndGroupPresence);
    }

    private boolean validateTeacherAndGroupPresence(Schedule schedule) {
        Teacher teacher = teacherService.getEntity(t -> Objects.equals(t.getId(), schedule.getTeacherId()));
        Group group = groupService.getEntity(g -> Objects.equals(g.getId(), schedule.getGroupId()));
        return Objects.nonNull(teacher) && Objects.nonNull(group);
    }
}


