package de.aittr.lmsbe.feedback.dto;

import de.aittr.lmsbe.feedback.utils.FeedbackVars;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@Builder
@Schema(description = "Data for FeedbackAnswer")
public class FeedbackAnswerDto {

    @Schema(description = "Question id", example = "1")
    @NotNull(message = "Question ID can't be null")
    private Long questionId;

    @Schema(description = "ID of the answer that was selected", example = "1")
    private Long answerId;

    @Schema(description = "Text from additional text field", example = "Student personal answer")
    @Length(max = FeedbackVars.PERSONAL_ANSWER_TEXT_LENGTH, message = "Personal answer text length must be less than or equal to {max}")
    private String answerText;
}
