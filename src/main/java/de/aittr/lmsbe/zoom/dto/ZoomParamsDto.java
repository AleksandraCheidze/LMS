package de.aittr.lmsbe.zoom.dto;


import de.aittr.lmsbe.model.LessonType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@Data
@Builder
public class ZoomParamsDto {

    @Schema(description = "Id of cohort, from 1 to 600", example = "[24, 25]")
    private List<Long> cohortIds;

    @Schema(description = "Lesson type. Available: LECTURE|CONSULTATION|ELECTIVE|PROJECT, another replaced by: UNDEFINED", example = "LECTURE")
    @Size(min = 1, max = 30)
    private String lessonType;

    public void setLessonType(String lessonType) {
        LessonType type = LessonType.getByName(lessonType);
        this.lessonType = (type != LessonType.UNDEFINED) ? type.name() : LessonType.UNDEFINED.name();
    }

    @Size(min = 1, max = 40)
    @Schema(description = "Available: BASIC_PROGRAMMING|GIT_AND_LINUX|BACK_END|FRONT_END|DBDEPLOYMENT|JOB_SEARCH|QA|QUIZ, another replaced by: UNDEFINED", example = "QA")
    private String lessonModule;

    @Schema(description = "Lesson number from 01-100", example = "07")
    private String lessonsNr;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 70)
    @Schema(description = "Lesson topic", example = "Appium inspector")
    private String lessonTopic;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 40)
    @Schema(description = "The meeting's agenda", example = "Lesson 45")
    private String agenda;

    @NotBlank
    @Size(min = 10, max = 10, message = "dateToStart must be 10 character")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "dateToStart pattern: 'yyyy-MM-dd'")
    @Schema(description = "date to start in pattern: 'yyyy-MM-dd'", example = "2024-03-29")
    private String dateToStart;

    @NotBlank
    @Size(min = 5, max = 5, message = "timeToStart must be 5 character")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "timeToStart pattern is:  'HH:mm'")
    @Schema(description = "time to start pattern is:  'HH:mm'", example = "13:45")
    private String timeToStart;

    @NotNull
    @Min(5)
    @Max(480)
    @Schema(description = "Meetings duration in minutes", example = "240")
    private Integer duration;
}
