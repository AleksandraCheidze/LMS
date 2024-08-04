package de.aittr.lmsbe.utils;

import java.util.regex.Pattern;

public class LessonNumberUtils {

    private LessonNumberUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final Pattern VALID_LESSON_NUMBER_PATTERN = Pattern.compile("(lesson|consultation)_\\d{2,}");
    private static final Pattern NON_DIGITS_PATTERN = Pattern.compile("\\D");

    /**
     * Checks if a given lesson number is valid.
     *
     * @param lessonNr the lesson number to validate
     * @return true if the lesson number is valid, false otherwise
     */
    public static boolean isLessonNumberValid(String lessonNr) {
        return VALID_LESSON_NUMBER_PATTERN.matcher(lessonNr).matches();
    }

    /**
     * Parses the numeric part of a lesson number.
     *
     * @param lessonNr the lesson number string
     * @return the numeric part of the lesson number as an integer
     * @throws NumberFormatException if the lesson number does not contain a valid numeric part
     */
    public static int parseLessonNumber(String lessonNr) {
        return Integer.parseInt(extractNumericPart(lessonNr));
    }

    /**
     * Extracts the numeric part from a lesson number string.
     *
     * @param lessonNr the lesson number string from which to extract the numeric part
     * @return the numeric part of the lesson number as a string
     */
    public static String extractNumericPart(String lessonNr) {
        return NON_DIGITS_PATTERN.matcher(lessonNr).replaceAll("");
    }

    /**
     * Validates and parses a lesson number.
     *
     * @param lessonNr the lesson number to validate and parse
     * @return the parsed lesson number as an integer, or 0 if the lesson number is invalid
     */
    public static int validateAndParseLessonNumber(String lessonNr) {
        if (isLessonNumberValid(lessonNr)) {
            return parseLessonNumber(lessonNr);
        }
        return 0;
    }
}
