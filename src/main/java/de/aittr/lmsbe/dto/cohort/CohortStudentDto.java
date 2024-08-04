package de.aittr.lmsbe.dto.cohort;

import de.aittr.lmsbe.dto.StudentDto;
import lombok.*;

import java.util.List;


@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CohortStudentDto {
    private CohortDto selectedCohort;
    private List<StudentDto> data;
}
