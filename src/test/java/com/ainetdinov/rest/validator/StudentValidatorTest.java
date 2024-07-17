package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.service.BaseServiceTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class StudentValidatorTest extends BaseServiceTest {
    private final StudentValidator validator = new StudentValidator();

    @Test
    void validate_ShouldReturnTrue_WhenValidGroup() {
        assertThat(validator.validate(defaultStudent()), Matchers.is(true));
    }

    @Test
    void validate_ShouldReturnTrue_WhenUuidISNull() {
        Student student = defaultStudent();
        student.setUuid(null);
        assertThat(validator.validate(student), Matchers.is(true));
    }

    @Test
    void validate_ShouldReturnFalse_WhenStudentNameNotStartWithCapitalLetter() {
        Student student1 = defaultStudent();
        Student student2 = defaultStudent();
        student1.setName("anton");
        student2.setSurname("doe");

        assertThat(validator.validate(student1), Matchers.is(false));
        assertThat(validator.validate(student2), Matchers.is(false));
    }

    @Test
    void validate_ShouldReturnFalse_WhenTeacherNameContainDigits() {
        Student student1 = defaultStudent();
        Student student2 = defaultStudent();
        student1.setName("Ant0n");
        student2.setSurname("Do3");

        assertThat(validator.validate(student1), Matchers.is(false));
        assertThat(validator.validate(student2), Matchers.is(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"+7 9091 192-21-57", "+376 909 192-21-57", "+8 909 192-21-57", "375 909 192-21-57"})
    void validate_ShouldReturnFalse_WhenInvalidPhone(String phone) {
        Student student = defaultStudent();
        student.setPhoneNumber(phone);
        assertThat(validator.validate(student), Matchers.is(false));
    }
}