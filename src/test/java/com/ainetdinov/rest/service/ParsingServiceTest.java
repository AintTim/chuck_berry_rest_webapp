package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class ParsingServiceTest extends BaseTest {
    private static final String validPath = "src/main/webapp/WEB-INF/resources/students.json";

    @Test
    void parseList_ShouldReturnParsedList_WhenValidPath() {
        ParsingService service = new ParsingService();
        assertThat(service.parseList(Path.of(validPath), Student.class), Matchers.instanceOf(List.class));
    }

    @Test
    void parseList_ShouldReturnNull_WhenFileNotExist() {
        ParsingService service = new ParsingService();
        Path invalidPath = Path.of(validPath.replace("students.json", "clowns.json"));
        assertThat(service.parseList(invalidPath, Student.class), Matchers.nullValue());
    }

    @Test
    void parse_ShouldReturnParsedStudent_WhenValidJsonString() {
        ParsingService service = new ParsingService();
        Student student = defaultStudent();
        assertThat(service.parse(student.toString(), new TypeReference<>() {}), Matchers.equalTo(student));
    }

    @Test
    void parse_ShouldReturnNull_WhenInvalidJsonString() {
        ParsingService service = new ParsingService();
        Student student = defaultStudent();
        assertThat(service.parse(student.toString() + "]", new TypeReference<>() {}), Matchers.nullValue());
    }
}