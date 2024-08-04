package de.aittr.lmsbe.service.cohort.search;

import de.aittr.lmsbe.dto.cohort.StudentCohortDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ICohortFilter {

    /**
     * Filters cohorts based on the specified scope and search term.
     *
     * @param scope         The scope of the search (e.g., email, firstName, lastName).
     * @param term          The search term.
     * @param searchPrimary Flag indicating whether to search in primary cohorts only.
     * @return A list of filtered cohort DTOs.
     */
    List<StudentCohortDto> filterByScope(String scope, String term, boolean searchPrimary);
}
