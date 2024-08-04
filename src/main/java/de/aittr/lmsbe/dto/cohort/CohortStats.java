package de.aittr.lmsbe.dto.cohort;

import lombok.Data;

@Data
public class CohortStats {
    protected final Long cohortId;
    protected final String cohortAlias;
    protected final int primaryStudents;
    protected final int otherStudents;
    protected final int totalLessons;
}
