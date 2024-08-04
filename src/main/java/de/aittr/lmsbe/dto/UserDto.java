package de.aittr.lmsbe.dto;

import de.aittr.lmsbe.dto.cohort.CohortDto;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "User")
public class UserDto implements Serializable {

    @Schema(description = "User id", example = "1")
    private Long id;

    @Schema(description = "User email", example = "example@gmail.com")
    private String email;

    @Schema(description = "User role: STUDENT, TEACHER, ADMIN", example = "STUDENT")
    private String role;

    @Schema(description = "User state: NOT_CONFIRMED, CONFIRMED", example = "CONFIRMED")
    private String state;

    @Schema(description = "User first name", example = "Jack")
    private String firstName;

    @Schema(description = "User last name", example = "Black")
    private String lastName;

    @Schema(description = "User country", example = "Germany")
    private String country;

    @Schema(description = "User phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "Cohort", example = "Cohort 23")
    private String primaryCohort;

    @Schema(description = "Cohorts list")
    private List<CohortDto> cohortList;

    @Schema(description = "E-Mail zoom Account or Zoom Account ID", example = "user@user.com or 812812iJSa2")
    private String zoomAccount;

    public static UserDto from(User user) {
        final String primaryCohort = user.getPrimaryCohort() != null ? user.getPrimaryCohort().getAlias() : null;
        Set<Cohort> cohorts = user.getCohorts();
        List<CohortDto> cohortDtoResults = null;
        if (!cohorts.isEmpty()) {
            cohortDtoResults = cohorts.stream()
                    .map(CohortDto::from)
                    .collect(Collectors.toList());
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .state(user.getState().name())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .country(user.getCountry())
                .phone(user.getPhone())
                .primaryCohort(primaryCohort)
                .cohortList(cohortDtoResults)
                .zoomAccount(user.getZoomAccount())
                .build();
    }

    public static List<UserDto> from(List<User> users) {
        return users.stream()
                .map(UserDto::from)
                .collect(Collectors.toList());
    }
}
