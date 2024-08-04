package de.aittr.lmsbe.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for NewLessonFeedback")
public class NewLessonFeedbackDto {

    @Schema(description = "Answers list")
    @NotEmpty
    @Valid
    private List<FeedbackAnswerDto> feedbackAnswerDtoList;
}
