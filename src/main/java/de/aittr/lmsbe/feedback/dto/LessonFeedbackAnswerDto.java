package de.aittr.lmsbe.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data of answer for lesson feedback question")
public class LessonFeedbackAnswerDto {

    @Schema(description = "Text of answer", example = "My name is John")
    private String answerText;

    @Schema(description = "If answer was checked by student = true", example = "true")
    boolean isChecked;
}
