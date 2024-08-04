package de.aittr.lmsbe.service;

import de.aittr.lmsbe.dto.LessonDto;
import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.model.*;
import de.aittr.lmsbe.repository.LessonRepository;
import de.aittr.lmsbe.service.cohort.CohortService;
import de.aittr.lmsbe.service.interfaces.ILessonService;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.aittr.lmsbe.utils.LessonNumberUtils.validateAndParseLessonNumber;

/**
 * @author Andrej Reutow
 * created on 18.11.2023
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class LessonService implements ILessonService {

    private final LessonRepository lessonRepository;
    private final CohortService cohortService;
    private final UsersService usersService;

    @Transactional
    @Override
    public Lesson create(String cohortAlias,
                         LessonModul lessonModul,
                         LessonType lessonType,
                         Integer lessonNr,
                         String topic,
                         String meetingHostEmail,
                         LocalDateTime meetingTime) {
        log.debug("Save lesson process for cohort: {}, lessonModul: {}, lessonType: {}, lessonNr: {}, topic: {}",
                cohortAlias, lessonModul, lessonType, lessonNr, topic);


        final Optional<User> teacherByEmail = usersService.findUserByEmail(meetingHostEmail);
        User teacher = teacherByEmail.orElseGet(
                () -> usersService.findUserByZoomEmail(meetingHostEmail).orElse(null)
        );

        final Cohort cohort = cohortService.getCohortByAliasOrNull(cohortAlias);

        final Lesson savedLesson = save(new Lesson(cohort,
                lessonModul,
                lessonType,
                lessonNr,
                meetingTime,
                topic,
                teacher));
        if (cohort != null) {
            cohort.addLesson(savedLesson);
            cohortService.save(cohort);
        }
        log.info("Lesson saved with id: {}", savedLesson.getId());

        return savedLesson;
    }

    @Transactional
    @Override
    public List<Lesson> findAllLessons(User currentUser,
                                       long cohortId,
                                       List<LessonModul> lessonModules,
                                       List<LessonType> lessonTypes,
                                       LocalDateTime from,
                                       LocalDateTime to) {
        Cohort cohort = cohortService.getCohortByIdOrThrow(cohortId);
        return lessonRepository.findAllForTeacherAndCohortByParams(currentUser, cohort,
                lessonModules,
                lessonTypes,
                from,
                to);
    }

    @Transactional
    @Override
    public List<LessonDto> getAllLessons(User currentUser,
                                         long cohortId,
                                         List<LessonModul> lessonModules,
                                         List<LessonType> lessonTypes,
                                         LocalDateTime from,
                                         LocalDateTime to) {
        return findAllLessons(currentUser, cohortId, lessonModules, lessonTypes, from, to).stream()
                .map(LessonDto::from)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public List<LessonDto> getAllLessons(long teacherId,
                                         long cohortId,
                                         List<LessonModul> lessonModules,
                                         List<LessonType> lessonTypes,
                                         LocalDateTime from,
                                         LocalDateTime to) {
        User teacher = usersService.getUserOrThrow(teacherId);
        return findAllLessons(teacher, cohortId, lessonModules, lessonTypes, from, to)
                .stream().map(LessonDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public LessonDto getLessonOrThrow(String groupName, String moduleName, String lessonType, Integer lessonNr) {
        return findLesson(groupName, moduleName, lessonType, lessonNr)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND,
                        "Lesson by cohort: " + groupName + " and number " + lessonNr + " not found"));
    }

    @Override
    public Optional<LessonDto> findLesson(String groupName, String moduleName, String lessonType, Integer lessonNr) {
        final Cohort selectedCohort = cohortService.getCohortByRepoNameOrThrow(groupName);
        final LessonModul selectedModule = LessonModul.getByName(moduleName);
        final LessonType selectedType = LessonType.getByName(lessonType);

        List<Lesson> cohortLessons = getLessonByCohortAndModule(selectedCohort,
                selectedModule,
                selectedType,
                lessonNr);
        if (cohortLessons.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(LessonDto.from(cohortLessons.get(0)));
        }
    }

    @Override
    public Lesson findLesson(String lessonModule, String lessonType, Integer lessonNr, Long cohortId) {
        LessonModul module = LessonModul.valueOf(lessonModule);
        LessonType type = LessonType.valueOf(lessonType);
        Cohort cohort = cohortService.getCohortByIdOrThrow(cohortId);
        return Optional.ofNullable(lessonRepository.findByCohortAndLessonModulAndLessonTypeAndLessonNr(cohort,
                        module,
                        type,
                        lessonNr))
                .orElseThrow(() -> new BadRequestException(String.format(
                        "Lesson not found: %s %s lessonNr: %d cohortId: %d ",
                        lessonModule, lessonType, lessonNr, cohortId)));
    }

    @Override
    public List<Lesson> getLessonByCohortAndModule(Cohort cohort,
                                                   LessonModul module,
                                                   LessonType lessonType,
                                                   Integer lessonNr) {
        return lessonRepository.findLessonByModulTypeNrAndCohort(module, lessonType, lessonNr, cohort);
    }


    @Override
    public Optional<LessonDto> getLectureLesson(String cohortRepoName,
                                                String moduleName,
                                                String lessonNr,
                                                LessonType lessonType) {
        return findLesson(cohortRepoName,
                moduleName,
                lessonType.getLessonTypeName(),
                validateAndParseLessonNumber(lessonNr));
    }

    @Override
    public List<Lesson> getByMeetingUUID(ZoomMeeting zoomMeeting) {
        return lessonRepository.findByMeeting(zoomMeeting);
    }

    @Override
    public Lesson save(Lesson lesson) {
        log.debug("Saving lesson: {}", lesson);
        Lesson savedLesson = lessonRepository.save(lesson);
        log.debug("Saved lesson: {}", savedLesson);
        return savedLesson;

    }
}
