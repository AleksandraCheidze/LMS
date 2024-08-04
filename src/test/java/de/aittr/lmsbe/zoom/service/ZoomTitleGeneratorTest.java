package de.aittr.lmsbe.zoom.service;

import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.model.Cohort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ZoomMeetingTitleGeneratorUnitTest {

    ZoomMeetingTitleGenerator underTest = new ZoomMeetingTitleGenerator();

    @DisplayName("Testing ZoomTitleGenerator with no cohorts")
    @Test
    void emptyCohort() {
        String lessonModule = "English";
        String lessonType = "interactive";
        String lessonNumber = "10";
        String lessonTopic = "Verbs";
        String expectedTitle = "cohort:\"999\", module: \"english\", type: \"interactive\", lesson: \"10\", topic: \"verbs\"";

        List<Cohort> selectedCohorts = new ArrayList<>();
        assertEquals(expectedTitle, underTest.generateLessonZoomTitle(selectedCohorts, lessonModule, lessonType, lessonNumber, lessonTopic));
    }

    @DisplayName("Testing ZoomTitleGenerator with one cohort")
    @Test
    void singleCohort() {
        String lessonModule = "English";
        String lessonType = "interactive";
        String lessonNumber = "10";
        String lessonTopic = "Verbs";
        String expectedTitle = "cohort:\"25\", module: \"english\", type: \"interactive\", lesson: \"10\", topic: \"verbs\"";
        String expectedTitle2 = "cohort:\"25\", module: \"english\", type: \"interactive\", lesson: \"10\", topic: \"verbs\"";
        String expectedTitle3 = "cohort:\"25 Java Pro\", module: \"english\", type: \"interactive\", lesson: \"10\", topic: \"verbs\"";

        List<Cohort> selectedCohorts = Collections.singletonList(Cohort.builder().name("Cohort25").build());
        List<Cohort> selectedCohorts2 = Collections.singletonList(Cohort.builder().name("Cohort 25").build());
        List<Cohort> selectedCohorts3 = Collections.singletonList(Cohort.builder().name("Cohort 25 Java Pro").build());

        assertEquals(expectedTitle, underTest.generateLessonZoomTitle(selectedCohorts, lessonModule, lessonType, lessonNumber, lessonTopic));
        assertEquals(expectedTitle2, underTest.generateLessonZoomTitle(selectedCohorts2, lessonModule, lessonType, lessonNumber, lessonTopic));
        assertEquals(expectedTitle3, underTest.generateLessonZoomTitle(selectedCohorts3, lessonModule, lessonType, lessonNumber, lessonTopic));
    }

    @DisplayName("Testing ZoomTitleGenerator with one cohort with alphabet number")
    @Test
    void singleCohortWithAlphabetNumber() {
        String lessonModule = "English";
        String lessonType = "interactive";
        String lessonNumber = "10";
        String lessonTopic = "Verbs";
        String expectedTitle = "cohort:\"25ELL\", module: \"english\", type: \"interactive\", lesson: \"10\", topic: \"verbs\"";

        List<Cohort> selectedCohorts = Collections.singletonList(Cohort.builder().name("Cohort25ELL").build());
        assertEquals(expectedTitle, underTest.generateLessonZoomTitle(selectedCohorts, lessonModule, lessonType, lessonNumber, lessonTopic));
    }

    @DisplayName("Testing ZoomTitleGenerator with one cohort with Cohort where C is lower case and with alphabet number")
    @Test
    void singleCohortLowerCaseAndAlphabetNumber() {
        String lessonModule = "English";
        String lessonType = "interactive";
        String lessonNumber = "10";
        String lessonTopic = "Verbs";
        String expectedTitle = "cohort:\"25ELL\", module: \"english\", type: \"interactive\", lesson: \"10\", topic: \"verbs\"";

        List<Cohort> selectedCohorts = Collections.singletonList(Cohort.builder().name("cohort25ELL").build());
        assertEquals(expectedTitle, underTest.generateLessonZoomTitle(selectedCohorts, lessonModule, lessonType, lessonNumber, lessonTopic));
    }

    @DisplayName("Testing ZoomTitleGenerator with one cohort with Cohort where C is lower case")
    @Test
    void singleCohortLowerCase() {
        String lessonModule = "English";
        String lessonType = "interactive";
        String lessonNumber = "10";
        String lessonTopic = "Verbs";
        String expectedTitle = "cohort:\"25\", module: \"english\", type: \"interactive\", lesson: \"10\", topic: \"verbs\"";

        List<Cohort> selectedCohorts = Collections.singletonList(Cohort.builder().name("cohort25").build());
        assertEquals(expectedTitle, underTest.generateLessonZoomTitle(selectedCohorts, lessonModule, lessonType, lessonNumber, lessonTopic));
    }


    @DisplayName("Testing ZoomTitleGenerator with multiple cohorts")
    @Test
    void multipleCohorts() {
        String lessonModule = "English";
        String lessonType = "interactive";
        String lessonNumber = "10";
        String lessonTopic = "Verbs";
        String expectedTitle = "cohort:[\"25\", \"26\"], module: \"english\", type: \"interactive\", lesson: \"10\", topic: \"verbs\"";

        List<Cohort> selectedCohorts = Arrays.asList(
                Cohort.builder().name("Cohort25").build(),
                Cohort.builder().name("Cohort26").build());
        assertEquals(expectedTitle, underTest.generateLessonZoomTitle(selectedCohorts, lessonModule, lessonType, lessonNumber, lessonTopic));
    }

    @DisplayName("Testing ZoomTitleGenerator with invalid lesson number, should throw an exception")
    @Test
    void invalidLessonNumber() {
        String lessonModule = "English";
        String lessonType = "interactive";
        String lessonNumber = "1";
        String lessonTopic = "Verbs";

        List<Cohort> selectedCohorts = Collections.singletonList(Cohort.builder().name("CohortEnglish").build());
        assertThrows(RestException.class, () ->
                underTest.generateLessonZoomTitle(selectedCohorts, lessonModule, lessonType, lessonNumber, lessonTopic));
    }
}
