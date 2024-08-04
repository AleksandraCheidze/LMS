package de.aittr.lmsbe.service.interfaces;

import de.aittr.lmsbe.dto.LessonDto;
import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.model.*;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface ILessonService {

    /**
     * Creates a new lesson and saves it to the repository.
     *
     * @param cohortAlias      The alias of the cohort.
     * @param lessonModul      The module of the lesson.
     * @param lessonType       The type of the lesson.
     * @param lessonNr         The number of the lesson.
     * @param topic            The topic of the lesson.
     * @param meetingHostEmail The email of the meeting host.
     * @param meetingTime      The time of the meeting.
     * @return The saved lesson.
     * @throws BadRequestException if an error occurs during creation.
     */
    Lesson create(final String cohortAlias,
                  final LessonModul lessonModul,
                  final LessonType lessonType,
                  final Integer lessonNr,
                  final String topic,
                  String meetingHostEmail,
                  LocalDateTime meetingTime);

    /**
     * Finds all lessons for a given user and cohort within specified parameters.
     *
     * @param currentUser   The current user.
     * @param cohortId      The ID of the cohort.
     * @param lessonModules The list of lesson modules.
     * @param lessonTypes   The list of lesson types.
     * @param from          The start date and time.
     * @param to            The end date and time.
     * @return The list of lessons.
     */
    List<Lesson> findAllLessons(User currentUser,
                                long cohortId,
                                List<LessonModul> lessonModules,
                                List<LessonType> lessonTypes,
                                LocalDateTime from,
                                LocalDateTime to);

    /**
     * Retrieves all lessons as DTOs for a given user and cohort within specified parameters.
     *
     * @param currentUser   The current user.
     * @param cohortId      The ID of the cohort.
     * @param lessonModules The list of lesson modules.
     * @param lessonTypes   The list of lesson types.
     * @param from          The start date and time.
     * @param to            The end date and time.
     * @return The list of lesson DTOs.
     */
    List<LessonDto> getAllLessons(User currentUser,
                                  long cohortId,
                                  List<LessonModul> lessonModules,
                                  List<LessonType> lessonTypes,
                                  LocalDateTime from,
                                  LocalDateTime to);

    /**
     * Retrieves all lessons as DTOs for a given teacher and cohort within specified parameters.
     *
     * @param teacherId     The ID of the teacher.
     * @param cohortId      The ID of the cohort.
     * @param lessonModules The list of lesson modules.
     * @param lessonTypes   The list of lesson types.
     * @param from          The start date and time.
     * @param to            The end date and time.
     * @return The list of lesson DTOs.
     */
    List<LessonDto> getAllLessons(long teacherId,
                                  long cohortId,
                                  List<LessonModul> lessonModules,
                                  List<LessonType> lessonTypes,
                                  LocalDateTime from,
                                  LocalDateTime to);

    /**
     * Retrieves a lesson DTO or throws an exception if not found.
     *
     * @param groupName  The name of the group.
     * @param moduleName The name of the module.
     * @param lessonType The type of the lesson.
     * @param lessonNr   The number of the lesson.
     * @return The lesson DTO.
     * @throws RestException if the lesson is not found.
     */
    LessonDto getLessonOrThrow(String groupName,
                               String moduleName,
                               String lessonType,
                               Integer lessonNr);

    /**
     * Finds a lesson DTO by group name, module name, lesson type, and lesson number.
     *
     * @param groupName  The name of the group.
     * @param moduleName The name of the module.
     * @param lessonType The type of the lesson.
     * @param lessonNr   The number of the lesson.
     * @return An optional containing the lesson DTO if found, otherwise empty.
     */
    Optional<LessonDto> findLesson(String groupName,
                                   String moduleName,
                                   String lessonType,
                                   Integer lessonNr);

    /**
     * Finds a lesson by module, type, number, and cohort ID.
     *
     * @param lessonModule The module of the lesson.
     * @param lessonType   The type of the lesson.
     * @param lessonNr     The number of the lesson.
     * @param cohortId     The ID of the cohort.
     * @return The found lesson.
     * @throws BadRequestException if the lesson is not found.
     */
    Lesson findLesson(String lessonModule,
                      String lessonType,
                      Integer lessonNr,
                      Long cohortId);

    /**
     * Finds lessons by cohort, module, type, and number.
     *
     * @param cohort     The cohort.
     * @param module     The module of the lesson.
     * @param lessonType The type of the lesson.
     * @param lessonNr   The number of the lesson.
     * @return The list of lessons.
     */
    List<Lesson> getLessonByCohortAndModule(Cohort cohort,
                                            LessonModul module,
                                            LessonType lessonType,
                                            Integer lessonNr);

    /**
     * Finds a lecture lesson DTO by cohort repo name, module name, lesson number, and lesson type.
     *
     * @param cohortRepoName The repository name of the cohort.
     * @param moduleName     The name of the module.
     * @param lessonNr       The number of the lesson.
     * @param lessonType     The type of the lesson.
     * @return An optional containing the lecture lesson DTO if found, otherwise empty.
     */
    Optional<LessonDto> getLectureLesson(String cohortRepoName,
                                         String moduleName,
                                         String lessonNr,
                                         LessonType lessonType);

    /**
     * Finds lessons by Zoom meeting.
     *
     * @param zoomMeeting The Zoom meeting.
     * @return The list of lessons.
     */
    List<Lesson> getByMeetingUUID(ZoomMeeting zoomMeeting);

    /**
     * Saves a lesson to the repository.
     *
     * @param lesson The lesson to save.
     * @return The saved lesson.
     */
    Lesson save(Lesson lesson);
}
