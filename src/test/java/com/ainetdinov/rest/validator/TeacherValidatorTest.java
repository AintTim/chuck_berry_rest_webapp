package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Teacher;
import com.ainetdinov.rest.service.BaseServiceTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeacherValidatorTest extends BaseServiceTest {
    private final TeacherValidator validator = new TeacherValidator();

    @Test
    void validate_ShouldReturnTrue_WhenValidGroup() {
        assertThat(validator.validate(defaultTeacher()), Matchers.is(true));
    }

    @Test
     void validate_ShouldReturnTrue_WhenUuidISNull() {
        Teacher teacher = defaultTeacher();
        teacher.setUuid(null);
        assertThat(validator.validate(teacher), Matchers.is(true));
    }

    @Test
    void validate_ShouldReturnFalse_WhenTeacherIsNull() {
        assertThat(validator.validate(null), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenTeacherNameNotStartWithCapitalLetter() {
        Teacher teacher = defaultTeacher();
        teacher.setName("anton");
        assertThat(validator.validate(teacher), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenTeacherNameContainDigits() {
        Teacher teacher = defaultTeacher();
        teacher.setName("Ant0n");
        assertThat(validator.validate(teacher), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenTeacherExperienceIsLowerThenZero() {
        Teacher teacher1 = defaultTeacher();
        Teacher teacher2 = defaultTeacher();
        teacher1.setExperience(-1);
        teacher2.setExperience(0);

        assertThat(validator.validate(teacher1), Matchers.is(false));
        assertThat(validator.validate(teacher2), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenTeacherSubjectsNull() {
        Teacher teacher = defaultTeacher();
        teacher.setSubjects(null);
        assertThat(validator.validate(teacher), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenTeacherSubjectsEmpty() {
        Teacher teacher = defaultTeacher();
        teacher.setSubjects(new ArrayList<>());
        assertThat(validator.validate(teacher), Matchers.is(false));
    }
}