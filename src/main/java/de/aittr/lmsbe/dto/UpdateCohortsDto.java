package de.aittr.lmsbe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class UpdateCohortsDto {
    @NotEmpty
    @Schema(description = "Cohort IDs to add to the student  ", example = "[21,22,23]")
    private List<@NotNull @Min(1) Long> cohortIds;
}
