package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Teacher;
import com.ainetdinov.rest.service.ValidatorService;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.ainetdinov.rest.constant.WebConstant.DIGIT_REGEX;
import static com.ainetdinov.rest.constant.WebConstant.UUID_REGEX;

public class GroupValidator implements ValidatorService<Group> {

    @Override
    public boolean validate(Group object) {
        Pattern pattern = Pattern.compile(DIGIT_REGEX);
        return Objects.nonNull(object)
                && pattern.matcher(object.getNumber()).matches()
                && validateUuid(object);
    }

    private boolean validateUuid(Group object) {
        if (Objects.isNull(object.getUuid())) {
            return true;
        } else {
            Pattern pattern = Pattern.compile(UUID_REGEX);
            return pattern.matcher(object.getUuid()).matches();
        }
    }

}
