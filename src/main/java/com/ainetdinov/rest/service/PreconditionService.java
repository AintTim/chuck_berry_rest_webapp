package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;

import java.util.List;
import java.util.Properties;

public class PreconditionService {
    private final Properties properties;

    public  PreconditionService(Properties properties) {
        this.properties = properties;
    }

    public boolean validateGroupSize(Group group) {
        return group.getStudents().size() < getLimit(WebConstant.MAX_GROUP_SIZE)
                && group.getStudents().size() >= getLimit(WebConstant.MIN_GROUP_SIZE);
    }

    public boolean validateGroupSchedule(Schedule schedule, ScheduleService scheduleService) {
        return getGroupScheduleLoad(schedule, scheduleService).size() < getLimit(WebConstant.MAX_GROUP_SCHEDULE_LOAD);
    }

    private List<Schedule> getGroupScheduleLoad(Schedule schedule, ScheduleService scheduleService) {
        return scheduleService.getEntities(g -> g.getUuid().equals(schedule.getGroup())
                && g.getStart().toLocalDate().equals(schedule.getStart().toLocalDate()));
    }

    private int getLimit(String property) {
        return Integer.parseInt(properties.getProperty(property));
    }

}
