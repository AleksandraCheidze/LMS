package de.aittr.lmsbe.feedback.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.aittr.lmsbe.feedback.utils.FeedbackVars;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(length = FeedbackVars.QUESTION_TEXT_LENGTH)
    private String questionText;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Answer> answers;

    private boolean isMultiCheck;

    private boolean isTextOn;

    private boolean isActive;

    private boolean isFinal;

    private boolean isLesson;

    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", isMultiCheck=" + isMultiCheck +
                ", isTextOn=" + isTextOn +
                ", isActive=" + isActive +
                ", isFinal=" + isFinal +
                ", isLesson=" + isLesson +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id != null && id.equals(question.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
