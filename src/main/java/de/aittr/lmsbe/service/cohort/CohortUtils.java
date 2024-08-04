package de.aittr.lmsbe.service.cohort;

import de.aittr.lmsbe.dto.cohort.CohortStats;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.Lesson;
import de.aittr.lmsbe.model.LessonType;
import de.aittr.lmsbe.model.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The CohortUtils class provides utility methods for working with cohorts and users.
 */
@Slf4j
public abstract class CohortUtils {

    private CohortUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Calculates the total number of primary students in a given cohort.
     *
     * @param cohort The cohort for which to calculate the total number of primary students.
     * @param users  The set of users in the cohort.
     * @return The total number of primary students in the cohort.
     */
    public static int getTotalPrimaryStudentsInCohort(@NonNull final Cohort cohort,
                                                      @NonNull final Set<User> users) {
        return (int) users.stream()
                .filter(user -> User.Role.STUDENT.equals(user.getRole()))
                .filter(user -> cohort.equals(user.getPrimaryCohort()))
                .count();
    }

    /**
     * Returns a list of lessons for a given teacher within a specified time range.
     *
     * @param currentUser The current user who is the teacher.
     * @param from        The start time of the range, inclusive.
     * @param to          The end time of the range, inclusive.
     * @param cohort      The cohort to retrieve the lessons from.
     * @return A list of lessons for the given teacher within the specified time range.
     * @throws NullPointerException if either the currentUser or from to is null
     */
    public static List<Lesson> getTeacherLessons(@NonNull final User currentUser,
                                                 @NonNull final LocalDateTime from,
                                                 @NonNull final LocalDateTime to,
                                                 Cohort cohort) {
        if (cohort == null || cohort.getLessons() == null || cohort.getLessons().isEmpty()) {
            log.debug("Cohort for teacher {} is null or doesn't contain any lessons", currentUser.getId());
            return new ArrayList<>();
        }

        return cohort.getLessons().stream()
                .filter(lesson -> lesson.getLessonTime() != null)
                .filter(lesson -> currentUser.equals(lesson.getTeacher())
                        && (from.isBefore(lesson.getLessonTime()) || from.equals(lesson.getLessonTime()))
                        && (to.isAfter(lesson.getLessonTime()) || to.equals(lesson.getLessonTime())))
                .collect(Collectors.toList());
    }

   /**
     * Returns the number of lessons of a specific type in a list of teacher lessons.
     *
     * @param teacherLessons The list of teacher lessons.
     * @param type           The type of lesson to count.
     * @return The count of lessons with the specified type.
     * @throws NullPointerException if either the teacherLessons list or type is null
     */
    public static long countLessonsByType(@NonNull final List<Lesson> teacherLessons,
                                          @NonNull final LessonType type) {
        return teacherLessons.stream()
                .filter(lesson -> type.equals(lesson.getLessonType()))
                .count();
    }

    /**
     * Calculates the statistics for each cohort based on the given list of cohorts and stores the results in the provided list.
     *
     * @param cohorts A list of cohorts for which the statistics need to be calculated
     * @param result  A list to store the calculated cohort statistics
     * @throws NullPointerException if either the cohorts or result list is null
     */
    public static void calculateCohortStats(@NonNull final List<Cohort> cohorts,
                                            @NonNull final List<CohortStats> result) {
        for (Cohort cohort : cohorts) {
            Set<User> users = cohort.getUsers();
            int totalPrimaryStudents = getTotalPrimaryStudentsInCohort(cohort, users);
            int totalLessons = cohort.getLessons().size();
            result.add(new CohortStats(cohort.getId(),
                    cohort.getAlias(),
                    totalPrimaryStudents,
                    (users.size() - totalPrimaryStudents),
                    totalLessons
            ));
        }
    }
}
