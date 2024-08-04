package de.aittr.lmsbe.dto.cohort;

import de.aittr.lmsbe.model.Cohort;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Cohort")
public class CohortDto implements Serializable {

    @Schema(description = "Cohort id", example = "1")
    private Long id;

    @Schema(description = "Cohort github repository", example = "cohort21")
    @NotNull
    @Size(min = 3, max = 30)
    private String githubRepository;

    @Schema(description = "Cohort name", example = "cohort21")
    @NotNull
    @Size(min = 3, max = 30)
    private String name;

    @Schema(description = "Cohort alias", example = "Cohort 21")
    @NotNull
    @Size(min = 3, max = 30)
    private String alias;

    public static CohortDto from(Cohort cohort) {
        if (cohort == null) {
            return new CohortDto();
        }
        return CohortDto.builder()
                .id(cohort.getId())
                .githubRepository(cohort.getGithubRepository())
                .name(cohort.getName())
                .alias(cohort.getAlias())
                .build();
    }

    public static List<CohortDto> from(List<Cohort> cohorts) {
        return cohorts.stream()
                .map(CohortDto::from)
                .collect(Collectors.toList());
    }
}
