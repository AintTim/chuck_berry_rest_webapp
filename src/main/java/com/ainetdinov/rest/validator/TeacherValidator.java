package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Teacher;
import com.ainetdinov.rest.service.ValidatorService;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.ainetdinov.rest.constant.WebConstant.*;

public class TeacherValidator implements ValidatorService<Teacher> {

    @Override
    public boolean validate(Teacher object) {
        return Objects.nonNull(object)
                && validateUuid(object)
                && validateName(object, CAPITAL_REGEX, true)
                && validateName(object, DIGIT_REGEX, false)
                && object.getExperience() > 0
                && Objects.nonNull(object.getSubjects())
                && !object.getSubjects().isEmpty();
    }

    private boolean validateName(Teacher object, String regex, boolean isPresent) {
        Pattern pattern = Pattern.compile(regex);
        if (isPresent) {
            return pattern.matcher(object.getName()).matches();
        } else {
            return !pattern.matcher(object.getName()).find();
        }
    }

    private boolean validateUuid(Teacher object) {
        if (Objects.isNull(object.getUuid())) {
            return true;
        } else {
            Pattern pattern = Pattern.compile(UUID_REGEX);
            return pattern.matcher(object.getUuid()).matches();
        }
    }
}
