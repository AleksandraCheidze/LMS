package de.aittr.lmsbe.dto;

import de.aittr.lmsbe.dto.cohort.CohortDto;
import de.aittr.lmsbe.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class StudentDto {

    @Schema(description = "Student id", example = "1")
    private Long studentId;

    @Schema(description = "Student email", example = "example@gmail.com")
    private String studentEmail;

    @Schema(description = "Student first name", example = "Jack")
    private String studentFirstName;

    @Schema(description = "Student last name", example = "Black")
    private String studentLastName;

    @Schema(description = "Student country", example = "Germany")
    private String studentCountry;

    @Schema(description = "Student phone number", example = "+1234567890")
    private String studentPhone;

    @Schema(description = "Student status", example = "CONFIRMED")
    private User.State studentStatus;

    @Schema(description = "Student primary cohort", example = "Cohort 23")
    private CohortDto studentPrimaryCohort;

    @Schema(description = "Student available cohorts list")
    private List<CohortDto> studentCohortList;

    public static StudentDto from(User student) {
        return StudentDto.builder()
                .studentId(student.getId())
                .studentEmail(student.getEmail())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName())
                .studentCountry(student.getCountry())
                .studentPhone(student.getPhone())
                .studentStatus(student.getState())
                .studentPrimaryCohort(CohortDto.from(student.getPrimaryCohort()))
                .studentCohortList(
                        student.getCohorts()
                                .stream()
                                .map(CohortDto::from)
                                .sorted(Comparator.comparing(CohortDto::getAlias))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
