package de.aittr.lmsbe.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data of question for lesson feedback")
public class LessonFeedbackQuestionDto {

    @Schema(description = "Question text", example = "What is tour name?")
    private String questionText;

    @Schema(description = "List of answers")
    private List<LessonFeedbackAnswerDto> answerList;

    @Schema(description = "Text from additional text field", example = "Student personal answer")
    private String textField;
}
