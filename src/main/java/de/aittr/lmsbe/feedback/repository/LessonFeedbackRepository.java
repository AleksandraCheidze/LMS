package de.aittr.lmsbe.feedback.repository;

import de.aittr.lmsbe.feedback.model.LessonFeedback;
import de.aittr.lmsbe.model.LessonModul;
import de.aittr.lmsbe.model.LessonType;
import de.aittr.lmsbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonFeedbackRepository extends JpaRepository<LessonFeedback, Long> {
    List<LessonFeedback> findAllByTeacher(User currentUser);

    @Query("SELECT lf FROM LessonFeedback lf WHERE " +
            "(:teacherId IS NULL OR lf.teacher.id = :teacherId) AND " +
            "(:studentId IS NULL OR lf.student.id = :studentId) AND " +
            "(:cohortId IS NULL OR lf.lesson.cohort.id = :cohortId) AND " +
            "(:lessonModule IS NULL OR lf.lesson.lessonModul = :lessonModule) AND " +
            "(:lessonType IS NULL OR lf.lesson.lessonType = :lessonType) AND " +
            "(:lessonNr IS NULL OR lf.lesson.lessonNr = :lessonNr) AND " +
            "(COALESCE(:startDate, lf.timestamp) <= lf.timestamp) AND " +
            "(lf.timestamp <= COALESCE(:endDate, CURRENT_TIMESTAMP))")
    List<LessonFeedback> findAllByParams(Long teacherId,
                                         Long studentId,
                                         Long cohortId,
                                         LessonModul lessonModule,
                                         LessonType lessonType,
                                         Integer lessonNr,
                                         LocalDateTime startDate,
                                         LocalDateTime endDate);
}
