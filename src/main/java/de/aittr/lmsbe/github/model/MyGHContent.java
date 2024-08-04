package de.aittr.lmsbe.github.model;

import de.aittr.lmsbe.github.dto.LessonCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kohsuke.github.GHContent;

/**
 * Represents a wrapper class for GitHub content and Lesson code.
 */
@AllArgsConstructor
@Getter
public class MyGHContent {
    /**
     * Represents a GitHub content object.
     */
    private GHContent ghContent;
    /**
     *
     */
    private LessonCode lessonCode;
}
