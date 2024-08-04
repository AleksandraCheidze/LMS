package de.aittr.lmsbe.dto.cohort;

import lombok.Value;

/**
 * Class representing the DTO (Data Transfer Object) for Teacher Cohort.
 */
@Value
public class TeacherCohortDto {

    /**
     * Represents the identifier for a cohort.
     */
    long cohortId;
    /**
     * The alias for a cohort.
     */
    String cohortAlias;
    /**
     * Represents the number of lessons in a teacher cohort.
     */
    int lessonsCount;
}
