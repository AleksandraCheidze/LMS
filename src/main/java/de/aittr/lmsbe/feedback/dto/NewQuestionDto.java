package de.aittr.lmsbe.feedback.dto;

import de.aittr.lmsbe.feedback.utils.FeedbackVars;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@Schema(description = "Data for new question")
public class NewQuestionDto {

    @Schema(description = "Text of question", example = "What is your name?")
    @NotBlank(message = "{field.isBlank}")
    @Length(max = FeedbackVars.QUESTION_TEXT_LENGTH, message = "Question text length must be less than or equal to {max}")
    private String questionText;

    @Schema(description = "Answers list")
    @NotEmpty
    private List<AnswerDto> answers;

    @Schema(description = "Multiple answers? (true = Multiple, false = Single)", example = "true")
    private boolean isMultiCheck;

    @Schema(description = "Show text area for personal answer option? (true = SHOW)", example = "true")
    private boolean isTextOn;

    @Schema(description = "Show this question in feedback? (true = SHOW)", example = "false")
    private boolean isActive;

    @Schema(description = "Prevent changes for this question? (true = Prevent)", example = "false")
    private boolean isFinal;

    @Schema(description = "Defines the scope of the question (true = Lesson, false = Week)", example = "true")
    private boolean isLesson;
}
