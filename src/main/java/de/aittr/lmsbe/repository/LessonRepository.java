package de.aittr.lmsbe.repository;

import de.aittr.lmsbe.model.*;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("select l from Lesson l where l.cohort = ?1 and l.lessonModul = ?2 and l.lessonType = ?3 and l.lessonNr = ?4")
    Lesson findByCohortAndLessonModulAndLessonTypeAndLessonNr(Cohort cohort, LessonModul lessonModul, LessonType lessonType, Integer lessonNr);

    @Query("SELECT l " +
            "FROM Lesson l " +
            "WHERE l.teacher = :teacher " +
            "AND l.cohort = :cohort " +
            "AND l.lessonModul in :lessonModules " +
            "AND l.lessonType in :lessonTypes " +
            "AND (l.lessonTime BETWEEN :from AND :to)" +
            "ORDER BY l.lessonTime")
    List<Lesson> findAllForTeacherAndCohortByParams(User teacher,
                                                    Cohort cohort,
                                                    List<LessonModul> lessonModules,
                                                    List<LessonType> lessonTypes,
                                                    LocalDateTime from,
                                                    LocalDateTime to);

    /**
     * Finds a lesson based on the module, type, number, and cohort.
     *
     * @param lessonModul The module of the lesson.
     * @param lessonType  The type of the lesson.
     * @param lessonNr    The number of the lesson.
     * @param cohort      The cohort of the lesson.
     * @return An optional lesson that matches the criteria.
     */
    @Query("select l from Lesson l " +
            "where l.lessonModul = ?1 " +
            "and l.lessonType = ?2 " +
            "and l.lessonNr = ?3 " +
            "and l.cohort = ?4")
    List<Lesson> findLessonByModulTypeNrAndCohort(LessonModul lessonModul, LessonType lessonType, Integer lessonNr, Cohort cohort);

    @Query("select l from Lesson l where l.zoomMeeting = ?1")
    List<Lesson> findByMeeting(ZoomMeeting zoomMeeting);
}
