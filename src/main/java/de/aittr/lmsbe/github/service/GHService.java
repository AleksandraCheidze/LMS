package de.aittr.lmsbe.github.service;

import de.aittr.lmsbe.github.dto.LessonMeta;
import de.aittr.lmsbe.github.model.GHLessonType;
import de.aittr.lmsbe.model.User;

import java.util.List;

public interface GHService {

    /**
     * Retrieves a list of lesson modules for a given cohort.
     *
     * @param cohort The cohort for which to retrieve lesson modules.
     * @return A list of lesson module names.
     */
    List<String> getLessonModuls(final String cohort);

    /**
     * Retrieves a list of lessons for a given cohort, module name, and github lesson type.
     *
     * @param cohort            The cohort for which to retrieve the lessons.
     * @param moduleName        The module name associated with the lessons.
     * @param ghLessonType  The github lesson type of the lessons.
     * @return A list of lesson names.
     */
    List<String> getLessons(final String cohort,
                            final String moduleName,
                            final GHLessonType ghLessonType);

    /**
     * Retrieves the metadata for a lesson.
     *
     * @param cohort             The cohort associated with the lesson.
     * @param moduleName         The module name associated with the lesson.
     * @param lessonNr           The lesson number.
     * @param currentUser        The current user.
     * @param ghLessonType   The GitHub lesson type.
     * @return The LessonMeta object containing the lesson metadata.
     */
    LessonMeta getlessonMeta(final String cohort,
                             final String moduleName,
                             final String lessonNr,
                             final User currentUser,
                             final GHLessonType ghLessonType);
}
