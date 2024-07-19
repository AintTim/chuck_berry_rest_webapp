package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.WebConstant;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.model.Student;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Properties;

@Log4j2
public class PreconditionService {
    private final Properties properties;

    public  PreconditionService(Properties properties) {
        this.properties = properties;
    }

    public boolean validateGroupAdd(Group group) {
        int size = group.getStudents().size();
        log.debug("Validate group {} size before add: {} (max: {}, min: {})", group.getUuid(), size, getLimit(WebConstant.MAX_GROUP_SIZE), getLimit(WebConstant.MIN_GROUP_SIZE));
        return size <= getLimit(WebConstant.MAX_GROUP_SIZE)
                && size >= getLimit(WebConstant.MIN_GROUP_SIZE);
    }

    public boolean validateGroupUpdate(List<Student> students, Group target) {
        int size = students.size() + target.getStudents().size();
        log.debug("Validate group {} size before update: {} (max: {}, min: {})", target.getUuid(), size, getLimit(WebConstant.MAX_GROUP_SIZE), getLimit(WebConstant.MIN_GROUP_SIZE));
        return size <= getLimit(WebConstant.MAX_GROUP_SIZE)
                && size >= getLimit(WebConstant.MIN_GROUP_SIZE);
    }

    public boolean validateGroupSchedule(Schedule schedule, ScheduleService scheduleService) {
        int size = getGroupScheduleLoad(schedule, scheduleService).size() + 1;
        log.debug("Validate group ({}) schedule load before add/update: {} (max: {})", schedule.getGroup(), size, getLimit(WebConstant.MAX_GROUP_SIZE));
        return size <= getLimit(WebConstant.MAX_GROUP_SCHEDULE_LOAD);
    }

    private List<Schedule> getGroupScheduleLoad(Schedule schedule, ScheduleService scheduleService) {
        return scheduleService.getEntities(g -> g.getGroup().equals(schedule.getGroup())
                && g.getStart().toLocalDate().equals(schedule.getStart().toLocalDate()));
    }

    private int getLimit(String property) {
        return Integer.parseInt(properties.getProperty(property));
    }

}
