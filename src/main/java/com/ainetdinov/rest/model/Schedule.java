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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@EqualsAndHashCode(exclude = {"uuid"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule implements Entity {
    private String uuid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime end;
    private String group;
    private String teacher;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("uuid", uuid)
                .append("start", start.format(formatter))
                .append("end", end.format(formatter))
                .append("group", group)
                .append("teacher", teacher)
                .toString();
    }
}
