package de.aittr.lmsbe.feedback.dto;

import de.aittr.lmsbe.feedback.model.Answer;
import de.aittr.lmsbe.feedback.model.Question;
import de.aittr.lmsbe.feedback.utils.FeedbackVars;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Question")
public class QuestionDto {

    @Schema(description = "Question id", example = "1")
    private Long id;

    @Schema(description = "Text of question", example = "What is your name?")
    @NotBlank(message = "{field.isBlank}")
    @Length(max = FeedbackVars.QUESTION_TEXT_LENGTH, message = "Question text length must be less than or equal to {max}")
    private String questionText;

    @Schema(description = "Answers list")
    private List<Answer> answers;

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

    @Schema(description = "Local Date Time (timestamp)", example = "2024-03-05 04:25:15.901731")
    private LocalDateTime timestamp;

    public static QuestionDto from(Question question) {
        return QuestionDto.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .answers(question.getAnswers())
                .isMultiCheck(question.isMultiCheck())
                .isTextOn(question.isTextOn())
                .isActive(question.isActive())
                .isFinal(question.isFinal())
                .isLesson(question.isLesson())
                .timestamp(question.getTimestamp())
                .build();
    }

    public static List<QuestionDto> from(List<Question> questionList) {
        return questionList.stream()
                .map(QuestionDto::from)
                .collect(Collectors.toList());
    }
}
