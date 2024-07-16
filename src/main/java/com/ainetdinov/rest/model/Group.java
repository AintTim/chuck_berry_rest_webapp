package com.ainetdinov.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Data
@Builder(toBuilder = true)
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@EqualsAndHashCode(exclude = {"uuid"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements Entity {
    private String uuid;
    private String number;
    private List<Student> students;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("uuid", uuid)
                .append("number", number)
                .append("students", students)
                .toString();
    }
}
