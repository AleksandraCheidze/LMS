package de.aittr.lmsbe.feedback.dto;

import de.aittr.lmsbe.feedback.utils.FeedbackVars;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Answer")
public class AnswerDto {

    @Schema(description = "Answer id", example = "1")
    private Long id;

    @Schema(description = "Text of answer", example = "My name is John")
    @Length(max = FeedbackVars.ANSWER_TEXT_LENGTH, message = "Answer text length must be less than or equal to {max}")
    private String answerText;

    @Schema(description = "Rate of answer (1 - 5)", example = "5")
    @Min(value = FeedbackVars.MIN_RATE, message = "{feedback.answer.rate.min}")
    @Max(value = FeedbackVars.MAX_RATE, message = "{feedback.answer.rate.max}")
    private int rate;

}
