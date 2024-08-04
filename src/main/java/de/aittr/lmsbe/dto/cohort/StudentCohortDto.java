package de.aittr.lmsbe.dto.cohort;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StudentCohortDto extends CohortStats {


    public StudentCohortDto(Long cohortId,
                            String cohortAlias,
                            int primaryStudents,
                            int otherStudents,
                            int totalLessons) {
        super(cohortId, cohortAlias, primaryStudents, otherStudents, totalLessons);
    }

    public static StudentCohortDto from(CohortStats cohortStats) {
        return new StudentCohortDto(cohortStats.getCohortId(),
                cohortStats.getCohortAlias(),
                cohortStats.getPrimaryStudents(),
                cohortStats.getOtherStudents(),
                cohortStats.getTotalLessons());
    }
}
