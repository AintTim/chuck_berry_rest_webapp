package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.service.ValidatorService;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.ainetdinov.rest.constant.WebConstant.UUID_REGEX;

public class HttpValidator implements ValidatorService<String> {
    @Override
    public boolean validate(String object) {
        Pattern pattern = Pattern.compile(UUID_REGEX);
        return !Objects.isNull(object) && pattern.matcher(object).matches();
    }
}
