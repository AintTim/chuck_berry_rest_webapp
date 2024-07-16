package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.service.ValidatorService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.ainetdinov.rest.constant.WebConstant.UUID_REGEX;

public class ScheduleValidator implements ValidatorService<Schedule> {

    @Override
    public boolean validate(Schedule object) {
        return Objects.nonNull(object)
                && validateDates(object)
                && validateUuid(object);
    }

    private boolean validateDates(Schedule object) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return object.getStart().isAfter(currentDateTime)
                && object.getEnd().isAfter(object.getStart());
    }

    private boolean validateUuid(Schedule object) {
        if (Objects.isNull(object.getUuid())) {
            return true;
        } else {
            Pattern pattern = Pattern.compile(UUID_REGEX);
            return pattern.matcher(object.getUuid()).matches()
                    && pattern.matcher(object.getGroup()).matches()
                    && pattern.matcher(object.getTeacher()).matches();
        }
    }
}
