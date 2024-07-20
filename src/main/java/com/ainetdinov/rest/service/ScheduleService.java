package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Schedule;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@Getter
public class ScheduleService extends EntityService<Schedule> {
    private final TeacherService teacherService;
    private final GroupService groupService;

    public ScheduleService(List<Schedule> schedules, ValidatorService<Schedule> validator, TeacherService teacherService, GroupService groupService) {
        super(schedules, validator);
        this.teacherService = teacherService;
        this.groupService = groupService;
    }

    public Schedule updateSchedule(Schedule updated, UUID uuid) {
        Schedule current = getEntity(uuid);
        log.info("Schedule {}: validation before update\ncurrent: {}\nupdated: {}", uuid.toString(), current, updated);
        if (validateEntity(current, Objects::nonNull)
                && validateEntity(updated, validator::validate, this::isUnique, this::validateTeacherAndGroupPresence)) {
            updated.setUuid(uuid.toString());
            entities.put(uuid, updated);
            log.info("Schedule {}: updated", updated.getUuid());
            return updated;
        } else {
            return null;
        }
    }

    public boolean addSchedule(Schedule schedule) {
        schedule.setUuid(generateUUID());
        log.info("New schedule: validation before add\n{}", schedule);
        if (validateEntity(schedule, validator::validate, this::isUnique, this::validateTeacherAndGroupPresence)) {
            entities.put(UUID.fromString(schedule.getUuid()), schedule);
            log.info("Schedule {}: added", schedule.getUuid());
            return true;
        } else {
            return false;
        }
    }

    private boolean validateTeacherAndGroupPresence(Schedule schedule) {
        boolean isTeacherPresent = teacherService.getEntities().containsKey(UUID.fromString(schedule.getTeacher()));
        boolean isGroupPresent = groupService.getEntities().containsKey(UUID.fromString(schedule.getGroup()));
        return isTeacherPresent && isGroupPresent;
    }
}


