package de.aittr.lmsbe.controller;

import de.aittr.lmsbe.model.LessonModul;
import de.aittr.lmsbe.model.LessonType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for common controller operations.
 */
public abstract class ControllerUtil {

    /**
     * Private constructor to prevent instantiation of the utility class.
     * Throws an IllegalStateException if invoked.
     */
    private ControllerUtil() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Calculates the start and end dates based on the provided LocalDate objects.
     *
     * @param from The start date.
     * @param to   The end date.
     * @return A DatesResult object containing the calculated start and end dates.
     */
    public static DatesResult calculateDatesResult(LocalDate from, LocalDate to) {
        LocalDateTime startDate = from == null
                ? LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN)
                : LocalDateTime.of(from, LocalTime.MIN);

        LocalDateTime endDate = to == null
                ? LocalDateTime.of(startDate.toLocalDate().withDayOfMonth(startDate.toLocalDate().lengthOfMonth()), LocalTime.MAX)
                : LocalDateTime.of(to, LocalTime.MAX);

        return new DatesResult(startDate, endDate);
    }


    /**
     * Retrieves a list of LessonType objects based on the provided list of type names.
     *
     * @param types The list of type names.
     * @return A list of LessonType objects corresponding to the provided type names.
     */
    public static List<LessonType> getLessonTypesByNames(List<String> types) {
        return types.stream()
                .map(LessonType::getByName)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of LessonModul objects based on the provided list of module names.
     *
     * @param modules The list of module names.
     * @return A list of LessonModul objects corresponding to the provided module names.
     */
    public static List<LessonModul> getLessonModules(List<String> modules) {
        return modules.stream()
                .map(LessonModul::getByName)
                .collect(Collectors.toList());
    }

    /**
     * Inner class representing the result of calculating dates.
     */
    public static class DatesResult {
        public final LocalDateTime from;
        public final LocalDateTime to;

        /**
         * Constructs a DatesResult object with the provided start and end dates.
         *
         * @param from The start date.
         * @param to   The end date.
         */
        public DatesResult(LocalDateTime from, LocalDateTime to) {
            this.from = from;
            this.to = to;
        }
    }
}
