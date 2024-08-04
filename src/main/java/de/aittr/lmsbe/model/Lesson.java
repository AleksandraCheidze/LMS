package de.aittr.lmsbe.model;

import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Andrej Reutow
 * created on 18.11.2023
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "lesson")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cohort_id")
    private Cohort cohort;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_modul", nullable = false)
    private LessonModul lessonModul;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", nullable = false)
    private LessonType lessonType;

    @Column(name = "lesson_nr")
    private Integer lessonNr;

    private LocalDateTime lessonTime;

    @NotNull
    private String lessonTopic;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "zoom_meeting_id")
    private ZoomMeeting zoomMeeting;

    public Lesson(Cohort cohort,
                  LessonModul lessonModul,
                  LessonType lessonType,
                  Integer lessonNr,
                  LocalDateTime lessonTime,
                  String lessonTopic,
                  User teacher) {
        this.cohort = cohort;
        this.lessonModul = lessonModul;
        this.lessonType = lessonType;
        this.lessonNr = lessonNr;
        this.lessonTopic = lessonTopic;
        this.lessonTime = lessonTime;
        this.teacher = teacher;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Lesson lesson = (Lesson) object;

        if (!Objects.equals(id, lesson.id)) return false;
        if (!Objects.equals(cohort, lesson.cohort)) return false;
        if (lessonModul != lesson.lessonModul) return false;
        if (lessonType != lesson.lessonType) return false;
        if (!Objects.equals(lessonNr, lesson.lessonNr)) return false;
        if (!Objects.equals(lessonTopic, lesson.lessonTopic)) return false;
        return Objects.equals(zoomMeeting, lesson.zoomMeeting);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (cohort != null ? cohort.hashCode() : 0);
        result = 31 * result + (lessonModul != null ? lessonModul.hashCode() : 0);
        result = 31 * result + (lessonType != null ? lessonType.hashCode() : 0);
        result = 31 * result + (lessonNr != null ? lessonNr.hashCode() : 0);
        result = 31 * result + (lessonTopic != null ? lessonTopic.hashCode() : 0);
        result = 31 * result + (zoomMeeting != null ? zoomMeeting.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", cohort=" + cohort +
                ", lessonModul=" + lessonModul +
                ", lessonType=" + lessonType +
                ", lessonNr=" + lessonNr +
                ", lessonTopic='" + lessonTopic + '\'' +
                ", zoomMeeting=" + (zoomMeeting == null ? "null" : zoomMeeting.getUuid()) +
                '}';
    }
}
