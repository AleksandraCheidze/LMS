package de.aittr.lmsbe.github.dto;

import de.aittr.lmsbe.dto.LessonDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Represents the metadata for each lesson.
 */
@Data
@AllArgsConstructor
@Schema(description = "Metadata for each lesson")
public class LessonMeta {

    @Schema(description = "Plan data for the lesson", example = "Plan 1 (as bytes)")
    private String planData;

    @Schema(description = "Theory data for the lesson", example = "Theory 1 (as bytes)")
    private String theoryData;

    @Schema(description = "Homework data for the lesson", example = "Homework 1 (as bytes)")
    private String homeworkData;

    @Schema(description = "List of lesson codes associated with a lesson")
    private List<LessonCode> lessonCode;

    @Schema(description = "Boolean value outlining if a video exists for the lesson", example = "true")
    private boolean isVideoExists;

    @Schema(description = "Boolean value outlining if the video is allowed for the lesson", example = "false")
    private boolean isVideoAllowed;

    @Schema(description = "Lesson archive data as Base64 string", example = "UEsDBBQAAAAIA...")
    private String archiveData;

    private LessonDto lessonDto;
}
