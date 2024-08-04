package de.aittr.lmsbe.feedback.model;

import de.aittr.lmsbe.model.Lesson;
import de.aittr.lmsbe.model.User;
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
@Table(name = "lesson_feedback")
public class LessonFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToMany
    @JoinTable(name = "feedback_answers",
            joinColumns = @JoinColumn(name = "lesson_feedback_id"),
            inverseJoinColumns = @JoinColumn(name = "feedback_answer_id"))
    private List<FeedbackAnswer> selectedAnswers;

    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "LessonFeedback{" +
                "id=" + id +
                ", student=" + student.getId() +
                ", teacher=" + teacher.getId() +
                ", lesson=" + lesson.getId() +
                ", selectedAnswers=" + selectedAnswers +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonFeedback that = (LessonFeedback) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
