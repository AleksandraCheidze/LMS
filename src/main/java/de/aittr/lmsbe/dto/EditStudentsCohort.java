package de.aittr.lmsbe.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EditStudentsCohort {
    private Long userId;
    private UpdateCohortsDto cohortList;
    private UpdateCohortDto primaryCohort;
}
