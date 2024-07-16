package com.ainetdinov.rest.model;

import com.ainetdinov.rest.constant.Subject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@EqualsAndHashCode(exclude = {"uuid"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Teacher implements Entity {
    private String uuid;
    private String name;
    private int experience;
    private List<Subject> subjects;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("experience", experience)
                .append("subjects", subjects)
                .toString();
    }
}
