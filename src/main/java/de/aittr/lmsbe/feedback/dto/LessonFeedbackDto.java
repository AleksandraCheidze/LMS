package de.aittr.lmsbe.feedback.dto;

import de.aittr.lmsbe.dto.LessonDto;
import de.aittr.lmsbe.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Data of lesson feedback")
public class LessonFeedbackDto {

    @Schema(description = "Student data")
    private UserDto student;

    @Schema(description = "Teacher data")
    private UserDto teacher;

    @Schema(description = "Lesson data")
    private LessonDto lesson;

    @Schema(description = "List of questions")
    private List<LessonFeedbackQuestionDto> questionList;

    @Schema(description = "timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Rates of answers")
    private int[] rates;

    @Schema(description = "Indicates that student has left his own answer")
    private boolean hasTextAnswer;

}
