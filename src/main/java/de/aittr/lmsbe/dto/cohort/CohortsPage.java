package de.aittr.lmsbe.dto.cohort;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Cohorts page")
public class CohortsPage {

    @Schema(description = "Cohorts list")
    private List<CohortDto> cohorts;

    @Schema(description = "Total cohorts count", example = "1")
    private Long count;

    @Schema(description = "Pages count", example = "3")
    private Integer pagesCount;
}
