package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.service.ValidatorService;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.ainetdinov.rest.constant.WebConstant.*;

public class StudentValidator implements ValidatorService<Student> {

    @Override
    public boolean validate(Student object) {
        return Objects.nonNull(object)
                && validateUuid(object)
                && validateNameAndSurname(object, CAPITAL_REGEX, true)
                && validateNameAndSurname(object, DIGIT_REGEX, false)
                && validatePhoneNumber(object);
    }

    private boolean validateNameAndSurname(Student object, String regex, boolean isPresent) {
        Pattern pattern = Pattern.compile(regex);
        if (isPresent) {
            return pattern.matcher(object.getName()).matches() && pattern.matcher(object.getSurname()).matches();
        } else {
            return !pattern.matcher(object.getName()).find() && !pattern.matcher(object.getSurname()).find();
        }
    }

    private boolean validatePhoneNumber(Student object) {
        Pattern pattern = Pattern.compile(String.format("%s|%s", BELARUS_PHONE_REGEX, RUSSIAN_PHONE_REGEX));
        return pattern.matcher(object.getPhoneNumber()).matches();
    }

    private boolean validateUuid(Student object) {
        if (Objects.isNull(object.getUuid())) {
            return true;
        } else {
            Pattern pattern = Pattern.compile(UUID_REGEX);
            return pattern.matcher(object.getUuid()).matches();
        }
    }
}
