package de.aittr.lmsbe.feedback.model;

import de.aittr.lmsbe.feedback.utils.FeedbackVars;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "feedback_answer")
public class FeedbackAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @Column(length = FeedbackVars.PERSONAL_ANSWER_TEXT_LENGTH)
    private String answerText;

    @Override
    public String toString() {
        return "FeedbackAnswer{" +
                "id=" + id +
                ", question=" + question.getQuestionText() +
                ", answer=" + (answer != null ? answer.getAnswerText() : null) +
                ", answerText='" + answerText + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackAnswer that = (FeedbackAnswer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
