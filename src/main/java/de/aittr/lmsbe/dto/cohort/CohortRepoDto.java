package de.aittr.lmsbe.dto.cohort;

import de.aittr.lmsbe.model.Cohort;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Cohort with Repository URL")
public class CohortRepoDto {

    @Schema(description = "Cohort id", example = "1")
    private Long id;

    @Schema(description = "Cohort github repository", example = "cohort21")
    private String githubRepository;

    @Schema(description = "Cohort name", example = "cohort21")
    private String name;

    @Schema(description = "Cohort alias", example = "Cohort 21")
    private String alias;

    @Schema(description = "URL of the GitHub repository", example = "https://github.com/ait-tr/cohort21")
    private String repositoryUrl;

    public static CohortRepoDto from(Cohort cohort, String repoUrl) {
        return CohortRepoDto.builder()
                .id(cohort.getId())
                .githubRepository(cohort.getGithubRepository())
                .name(cohort.getName())
                .alias(cohort.getAlias())
                .repositoryUrl(repoUrl)
                .build();
    }
}
