package de.aittr.lmsbe.zoom.service;

import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.zoom.dto.ZoomParamsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * The ZoomTitleGenerator class generates a lesson Zoom title based on the provided parameters.
 */
@Service
@Slf4j
public class ZoomMeetingTitleGenerator {

    private static final String PREFIX_TEMPLATE = "\"%s\"";

    /**
     * Generates a lesson Zoom title based on the provided parameters.
     *
     * @param selectedCohorts The selected cohorts for the lesson.
     * @param lessonModule    The module of the lesson.
     * @param lessonType      The type of the lesson.
     * @param lessonNr        The number of the lesson.
     * @param lessonTopic     The topic of the lesson.
     * @return The generated lesson Zoom title.
     * @throws RestException if the lesson number is invalid.
     */
    public String generateLessonZoomTitle(List<Cohort> selectedCohorts,
                                          String lessonModule,
                                          String lessonType,
                                          String lessonNr,
                                          String lessonTopic) {
        log.debug("Starting generateLessonZoomTitle process with module: {}, type: {}, number: {}, topic: {}",
                lessonModule, lessonType, lessonNr, lessonTopic);

        String cohortPrefix = getCohortPrefix(selectedCohorts);
        validateLessonNumber(lessonNr);

        String lessonModulePrefix = String.format(PREFIX_TEMPLATE, lessonModule.toLowerCase());
        String lessonTypePrefix = String.format(PREFIX_TEMPLATE, lessonType.toLowerCase());
        String lessonNrPrefix = String.format(PREFIX_TEMPLATE, lessonNr.toLowerCase());
        String lessonTopicPrefix = String.format(PREFIX_TEMPLATE, lessonTopic.toLowerCase());
        String result = String.format("cohort:%s, module: %s, type: %s, lesson: %s, topic: %s",
                cohortPrefix, lessonModulePrefix, lessonTypePrefix, lessonNrPrefix, lessonTopicPrefix);

        log.debug("Generated lesson zoom title: {}", result);

        return result;
    }

    public String generateLessonZoomTitle(final List<Cohort> selectedCohorts, final ZoomParamsDto zoomParamsDto) {
        return generateLessonZoomTitle(new ArrayList<>(selectedCohorts),
                zoomParamsDto.getLessonModule(),
                zoomParamsDto.getLessonType(),
                zoomParamsDto.getLessonsNr(),
                zoomParamsDto.getLessonTopic());
    }

    private String getCohortPrefix(List<Cohort> selectedCohorts) {
        Function<Cohort, String> removeCohort = input -> {
            String name = Optional.ofNullable(input.getName()).orElse("");
            return name.replaceAll("(?i)cohort", "");
        };

        switch (selectedCohorts.size()) {
            case 0:
                log.debug("No cohorts are available, generating default cohort prefix");
                return String.format(PREFIX_TEMPLATE, "999");
            case 1:
                log.debug("Single cohort is available, generating cohort prefix");
                return String.format(PREFIX_TEMPLATE, removeCohort.apply(selectedCohorts.get(0)).trim());
            default:
                log.debug("Several cohorts are available, generating cohort prefix");
                return selectedCohorts.stream()
                        .map(name -> removeCohort.apply(name).trim())
                        .collect(Collectors.joining("\", \"", "[\"", "\"]"));
        }
    }

    private void validateLessonNumber(String lessonNr) {
        if (!lessonNr.matches("\\d{2,}")) {
            log.error("Invalid value for lesson number. Value: <{}>", lessonNr);
            throw new RestException(HttpStatus.BAD_REQUEST, "Invalid value for lesson number. Value: <" + lessonNr + ">");
        }
    }
}
