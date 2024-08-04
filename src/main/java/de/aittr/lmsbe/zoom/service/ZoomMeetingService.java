package de.aittr.lmsbe.zoom.service;

import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.model.*;
import de.aittr.lmsbe.service.LessonService;
import de.aittr.lmsbe.service.UsersService;
import de.aittr.lmsbe.service.cohort.CohortService;
import de.aittr.lmsbe.service.interfaces.IGoogleService;
import de.aittr.lmsbe.utils.AppUtils;
import de.aittr.lmsbe.zoom.dto.ZoomMeetingDto;
import de.aittr.lmsbe.zoom.dto.ZoomMeetingInfoDto;
import de.aittr.lmsbe.zoom.dto.ZoomParamsDto;
import de.aittr.lmsbe.zoom.entity.MeetingType;
import de.aittr.lmsbe.zoom.entity.ProcessedZoomVideo;
import de.aittr.lmsbe.zoom.entity.ZoomMeeting;
import de.aittr.lmsbe.zoom.mapper.ZoomMeetingMapper;
import de.aittr.lmsbe.zoom.meeting.ZoomMeetingResponse;
import de.aittr.lmsbe.zoom.meeting.ZoomMeetingSettings;
import de.aittr.lmsbe.zoom.model.LessonTopicObject;
import de.aittr.lmsbe.zoom.model.json.ZoomObjectData;
import de.aittr.lmsbe.zoom.repository.ZoomMeetingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.aittr.lmsbe.model.User.Role.ADMIN;
import static de.aittr.lmsbe.model.User.Role.TEACHER;
import static de.aittr.lmsbe.zoom.entity.MeetingType.EXTERN;
import static de.aittr.lmsbe.zoom.entity.MeetingType.INTERN;
import static de.aittr.lmsbe.zoom.mapper.ZoomMeetingMapper.mapTo;

/**
 * @author Andrej Reutow
 * created on 17.11.2023
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ZoomMeetingService {

    private final ZoomService zoomService;
    private final ZoomMeetingRepository zoomMeetingRepository;
    private final ProcessedZoomVideoService processedZoomVideoService;
    private final LessonService lessonService;
    private final CohortService cohortService;
    private final UsersService usersService;
    private final ZoomMeetingTitleGenerator zoomMeetingTitleGenerator;
    private final IGoogleService googleService;

    public List<ZoomMeetingDto> getMeetingsByUser(final User currentUser, LocalDate from, LocalDate to) {
        if (from == null) {
            log.warn("Get Meeting DTO By user: from is null");
            from = LocalDate.now().withDayOfMonth(1);
            log.debug("Updated from date: '{}'", from);
        }

        if (to == null) {
            log.warn("Get Meeting DTO By user: to is null");
            to = from.withDayOfMonth(from.lengthOfMonth());
            log.debug("Updated to date: '{}'", to);
        }

        log.debug("Get Meeting DTO By user: '{}', from : '{}', to: '{}'", currentUser, from, to);

        final List<User.Role> userRoles = List.of(TEACHER, ADMIN);
        final List<ZoomMeetingDto> userMeetings = zoomMeetingRepository.findAllForUserAndDates(currentUser,
                        LocalDateTime.of(from, LocalTime.MIN),
                        LocalDateTime.of(to, LocalTime.MAX),
                        userRoles)
                .stream()
                .map(ZoomMeetingDto::from)
                .collect(Collectors.toList());

        log.debug("Found user meetings for roles: '{}' '{}'", userRoles, userMeetings.size());
        log.debug("Returning user meetings for '{}'", userMeetings);
        return userMeetings;
    }

    public ZoomMeetingInfoDto getMeetingInfoByMeetingUUID(final String meetingUUID) {
        log.debug("Get Meeting Info By meetingUUID: '{}'", meetingUUID);
        final ZoomMeeting selectedMeeting = getById(meetingUUID);

        final List<Lesson> lessonServiceByMeetingUUID = lessonService.getByMeetingUUID(selectedMeeting);
        log.debug("Lessons by meeting found: '{}'", lessonServiceByMeetingUUID);
        return ZoomMeetingInfoDto.from(selectedMeeting, lessonServiceByMeetingUUID);
    }

    public ZoomMeeting getById(String meetingUUID) {
        log.debug("Find meeting by meetingUUID '{}'", meetingUUID);
        ZoomMeeting foundMeeting = zoomMeetingRepository.findById(meetingUUID)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "Meeting by UUID: '" + meetingUUID + "' not found"));
        log.debug("Meeting by meetingUUID '{}' found '{}'", meetingUUID, foundMeeting);
        return foundMeeting;
    }

    @Transactional
    public ZoomMeeting createAndSaveMeeting(final ZoomMeetingSettings zoomMeetingSettings, final MeetingType meetingType, User user) {
        final ZoomMeetingResponse zoomMeetingResponse = zoomService.createMeeting(zoomMeetingSettings, user);
        final ZoomMeeting zoomMeeting = mapTo(zoomMeetingResponse, meetingType, user);
        return zoomMeetingRepository.save(zoomMeeting);
    }

    @Transactional
    public ZoomMeetingDto createAndSaveMeeting(final ZoomMeetingSettings zoomMeetingSettings, User user) {
        final ZoomMeeting savedMeeting = createAndSaveMeeting(zoomMeetingSettings, INTERN, user);
        return ZoomMeetingDto.from(savedMeeting);
    }

    @Transactional
    public ZoomMeetingInfoDto createAndSaveMeetingWithLesson(final ZoomParamsDto zoomParamsDto, User user) {
        log.debug("Creating and saving meeting with lesson for user: {}", user.getEmail());
        User existingUser = usersService.getUserByEmailOrThrow(user.getEmail());
        Set<Cohort> selectedCohorts = cohortService.findByIdIn(zoomParamsDto.getCohortIds());
        int selectedCohortCount = zoomParamsDto.getCohortIds().size();
        int selectedCohortSize = selectedCohorts.size();
        log.debug("Cohorts declared {}, Cohorts found in db {}", selectedCohortCount, selectedCohortSize);

        if (selectedCohortCount != selectedCohortSize) {
            log.warn("Not all cohorts were found.");
            log.warn("Declared ids: {}, found ids: {}",
                    zoomParamsDto.getCohortIds(), selectedCohorts.stream()
                            .map(Cohort::getId)
                            .collect(Collectors.toList()));
        }

        //todo remove generation when fully integrated with conferences and lessons (start)
        String generatedZoomTitle = zoomMeetingTitleGenerator.generateLessonZoomTitle(new ArrayList<>(selectedCohorts),
                zoomParamsDto);
        zoomParamsDto.setAgenda(generatedZoomTitle);
        //todo remove generation when fully integrated with conferences and lessons (end)

        log.debug("About to create Zoom meeting with parameters: {}", zoomParamsDto);
        final ZoomMeetingResponse zoomMeetingResponse = zoomService.createParamMeeting(zoomParamsDto, existingUser);

        final List<Lesson> savedLessons = new ArrayList<>();
        for (Cohort cohort : selectedCohorts) {
            log.debug("Creating and saving lesson for Cohort: {}", cohort);
            Lesson lesson = new Lesson(
                    cohort,
                    LessonModul.getByName(zoomParamsDto.getLessonModule()),
                    LessonType.getByName(zoomParamsDto.getLessonType()),
                    Integer.parseInt(zoomParamsDto.getLessonsNr()),
                    AppUtils.parseZoomDateTime(zoomMeetingResponse.getStartTime()),
                    zoomParamsDto.getLessonTopic(),
                    user
            );
            log.debug("Saving lesson for cohort: id: {}, name: {}", cohort.getId(), cohort.getName());
            Lesson savedLesson = lessonService.save(lesson);
            cohort.addLesson(savedLesson);
            cohortService.save(cohort);
            log.info("Successfully saved lesson for cohort: id: {}, name: {}", cohort.getId(), cohort.getName());
            savedLessons.add(savedLesson);
        }

        ZoomMeeting savedMeeting = mapTo(zoomMeetingResponse, INTERN, user);
        ZoomMeeting createdMeeting = zoomMeetingRepository.save(savedMeeting);
        log.debug("Successfully created Zoom meeting for the user email: {}", user.getEmail());

        for (Lesson savedLesson : savedLessons) {
            log.debug("Binding zoom meeting: {} to lesson: {}", createdMeeting.getMeetingId(), savedLesson.getId());
            savedLesson.setZoomMeeting(createdMeeting);
            createdMeeting.getLessons().add(savedLesson);
        }

        log.info("Zoom meeting and lessons successfully created and saved.");

        String authorizationUrl = googleService.getAuthorizationUrl(createdMeeting.getUuid());
        createdMeeting.setGoogleAuthorizationUrl(authorizationUrl);
        return ZoomMeetingInfoDto.from(createdMeeting, savedLessons);
    }

    @Transactional
    public void saveExternMeeting(final ZoomObjectData zoomObjectData,
                                  final List<ProcessedZoomVideo> processedZoomVideos,
                                  final LessonTopicObject lessonTopicObject) {

        log.info("Attempting to save Meeting");
        final List<ProcessedZoomVideo> savedVideos = processedZoomVideoService.saveProcessedZoomVideos(processedZoomVideos);
        final ZoomMeeting zoomMeeting = zoomMeetingRepository
                .findById(zoomObjectData.getUuid())
                .orElseGet(() -> saveZoomMeeting(ZoomMeetingMapper.from(zoomObjectData, savedVideos, EXTERN)));

        for (ProcessedZoomVideo processedVideo : savedVideos) {
            processedVideo.setZoomMeeting(zoomMeeting);
        }

        final List<Lesson> filteredMeetingLessons = getListOfLessonsAssociatedWithMeeting(zoomMeeting);

        if (filteredMeetingLessons.isEmpty()) {
            for (String cohortAlias : lessonTopicObject.getCohort()) {
                addNewLesson(zoomMeeting, lessonTopicObject, zoomObjectData, cohortAlias);
            }
        } else {
            for (String cohortAlias : lessonTopicObject.getCohort()) {
                List<Lesson> filteredMeetingCohortLessons = getListOfCohortLessons(filteredMeetingLessons, cohortAlias);
                if (filteredMeetingCohortLessons.isEmpty()) {
                    addNewLesson(zoomMeeting, lessonTopicObject, zoomObjectData, cohortAlias);
                }
            }
        }

        log.info("Meeting saved: {}", zoomMeeting);
    }

    private List<Lesson> getListOfLessonsAssociatedWithMeeting(ZoomMeeting zoomMeeting) {
        return zoomMeeting.getLessons().stream()
                .filter(lesson -> zoomMeeting.equals(lesson.getZoomMeeting()))
                .collect(Collectors.toList());
    }

    private List<Lesson> getListOfCohortLessons(List<Lesson> existingMeetingLessons, String cohortAlias) {
        return existingMeetingLessons.stream()
                .filter(ml -> ml.getCohort() != null && ml.getCohort().getAlias().endsWith(cohortAlias))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveInvalidMeeting(final ZoomObjectData zoomObjectData,
                                   final List<ProcessedZoomVideo> processedZoomVideos) {

        final List<ProcessedZoomVideo> savedVideos = processedZoomVideoService.saveProcessedZoomVideos(processedZoomVideos);
        log.debug("processedZoomVideos for persisting: {}", savedVideos);

        final ZoomMeeting zoomMeeting = ZoomMeetingMapper.from(zoomObjectData, savedVideos, EXTERN);

        log.debug("Persist zoom meeting: {}", zoomMeeting);
        final ZoomMeeting saved = saveZoomMeeting(zoomMeeting);

        for (ProcessedZoomVideo processedZoomVideo : savedVideos) {
            log.debug("Set meeting with UUID '{}' for processed video {}", zoomMeeting.getUuid(), processedZoomVideo);
            processedZoomVideo.setZoomMeeting(saved);
        }
        log.info("Meeting saved: {}", saved);
    }

    @Transactional
    public ZoomMeeting saveZoomMeeting(ZoomMeeting zoomMeeting) {
        if (zoomMeeting.getUser() == null) {
            usersService.findUserByEmail(zoomMeeting.getHostEmail())
                    .ifPresent(zoomMeeting::setUser);
        }
        return zoomMeetingRepository.save(zoomMeeting);
    }

    private void addNewLesson(ZoomMeeting zoomMeeting, LessonTopicObject lessonTopicObject, ZoomObjectData zoomObjectData, String cohortAlias) {
        final Lesson newLesson = createAndSaveLesson(cohortAlias,
                lessonTopicObject,
                zoomObjectData.getTopic(),
                zoomObjectData.getHostEmail(),
                zoomMeeting.getMeetingTime());
        newLesson.setZoomMeeting(zoomMeeting);
    }

    private Lesson createAndSaveLesson(String cohortAlias,
                                       LessonTopicObject lessonTopicObject,
                                       String topic,
                                       String meetingHostEmail,
                                       LocalDateTime meetingTime) {
        final LessonModul lessonModul = LessonModul.getByName(lessonTopicObject.getModule());
        final LessonType lessonType = LessonType.getByName(lessonTopicObject.getType());
        Integer lessonNr = null;
        String cleanedString = lessonTopicObject.getLesson() != null ?
                lessonTopicObject.getLesson().replaceAll("[^0-9]", "")
                : "-1";
        try {
            lessonNr = Integer.parseInt(cleanedString);
        } catch (NumberFormatException e) {
            log.warn("Parse error: value for lesson number '{}' is not a integer", lessonTopicObject.getLesson());
        }
        return lessonService.create(cohortAlias, lessonModul, lessonType, lessonNr, topic, meetingHostEmail, meetingTime);
    }
}
