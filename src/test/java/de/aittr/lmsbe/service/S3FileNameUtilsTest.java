package de.aittr.lmsbe.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static de.aittr.lmsbe.zoom.utils.S3FileNameUtils.createDirectoryPrefixForValidTopic;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class S3FileNameUtilsTest {

    @Test
    void createDirectoryPrefixForValidTopic_lessonNumberSupplied() {

        String module = "basic_programming";
        String lesson = "25";
        String lessonType = "lecture";
        String cohort = "25";
        String startTime = "2023-06-26T15:31:21Z";

        assertEquals(
                "cohort_25/basic_programming/lecture/25/",
                createDirectoryPrefixForValidTopic(cohort, module, lessonType, lesson, startTime)
        );
    }

    @Test
    void createDirectoryPrefixForValidTopic_lessonNumberNotSupplied() {
        String module = "basic_programming";
        String lesson = "";
        String lessonType = "consultation";
        String cohort = "25";

        String startTime = "2023-06-26T15:31:21Z";

        createDirectoryPrefixForValidTopic(cohort, module, lessonType, lesson, startTime);

        assertEquals(
                "cohort_25/basic_programming/consultation/2023-06-26/",
                createDirectoryPrefixForValidTopic(cohort, module, lessonType, lesson, startTime)
        );
    }

    @Test
    void createDirectoryPrefixForValidTopic_lessonNumberNotSuppliedCohortNameMultipleWords() {
        String module = "basic_programming";
        String lesson = "";
        String lessonType = "consultation";
        String cohort = "25 Java Pro";

        String startTime = "2023-06-26T15:31:21Z";

        createDirectoryPrefixForValidTopic(cohort, module, lessonType, lesson, startTime);

        assertEquals(
                "cohort_25 Java Pro/basic_programming/consultation/2023-06-26/",
                createDirectoryPrefixForValidTopic(cohort, module, lessonType, lesson, startTime)
        );
    }
}
