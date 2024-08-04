package de.aittr.lmsbe.feedback.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.aittr.lmsbe.feedback.utils.FeedbackVars;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "answer")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @JsonBackReference
    @NotNull
    private Question question;

    @Column(length = FeedbackVars.ANSWER_TEXT_LENGTH)
    @NotBlank
    private String answerText;

    private int rate;

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", questionId=" + (question != null ? question.getId() : null) +
                ", answerText='" + answerText + '\'' +
                ", rate=" + rate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return Objects.equals(id, answer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
