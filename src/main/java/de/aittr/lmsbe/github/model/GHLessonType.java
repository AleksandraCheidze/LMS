package de.aittr.lmsbe.github.model;

import de.aittr.lmsbe.model.LessonType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the type of lesson in GitHub.
 */
@RequiredArgsConstructor
@Getter
public enum GHLessonType {
    /**
     * Represents the type of lesson in GitHub.
     */
    LESSON("lesson", "lesson_", LessonType.LECTURE),
    /**
     * Represents the CONSULTATION lesson type in GitHub.
     */
    CONSULTATION("consultation", "consultation_", LessonType.CONSULTATION);

    /**
     * Represents the path of a directory.
     */
    private final String path;
    private final String pathsSuffix;
    private final LessonType lessonType;
}
