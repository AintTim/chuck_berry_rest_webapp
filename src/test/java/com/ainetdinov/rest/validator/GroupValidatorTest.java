package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.service.BaseTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class GroupValidatorTest extends BaseTest {
    private final GroupValidator groupValidator = new GroupValidator();

    @Test
    void validate_ShouldReturnTrue_WhenValidGroup() {
        assertThat(groupValidator.validate(defaultGroup()), Matchers.is(true));
    }

    @Test
    void validate_ShouldReturnTrue_WhenNullUuid() {
        Group group = defaultGroup();
        defaultGroup().setUuid(null);
        assertThat(groupValidator.validate(group), Matchers.is(true));
    }

    @Test
    void validate_ShouldReturnFalse_WhenGroupIsNull() {
        assertThat(groupValidator.validate(null), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenPhoneContainsLetters() {
        Group group = defaultGroup();
        group.setNumber(group.getNumber() + "abc");
        assertThat(groupValidator.validate(group), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenInvalidUuid() {
        Group group = defaultGroup();
        group.setUuid(group.getUuid() + "abc");
        assertThat(groupValidator.validate(group), Matchers.is(false));
    }
}