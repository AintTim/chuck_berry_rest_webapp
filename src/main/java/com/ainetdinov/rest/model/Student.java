package com.ainetdinov.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;

@Data
@Jacksonized
@Builder
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@EqualsAndHashCode(exclude = {"uuid"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Student implements Entity{
    private String uuid;
    private String name;
    private String surname;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String phoneNumber;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("surname", surname)
                .append("birthDate", birthDate)
                .append("phoneNumber", phoneNumber)
                .toString();
    }
}
