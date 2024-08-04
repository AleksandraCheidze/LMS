package de.aittr.lmsbe.service.cohort.search.startegy;

import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.repository.CohortsRepository;

import java.util.List;

/**
 * Implementation of the CohortSearchStrategy interface for searching cohorts by student last name.
 */
public class LastNameSearchStrategy implements CohortSearchStrategy {
    private final CohortsRepository cohortsRepository;

    /**
     * Constructs a LastNameSearchStrategy with the provided CohortsRepository.
     *
     * @param cohortsRepository The repository for accessing cohort data.
     */
    public LastNameSearchStrategy(CohortsRepository cohortsRepository) {
        this.cohortsRepository = cohortsRepository;
    }

    /**
     * Searches for cohorts based on the student last name.
     *
     * @param term          The search term (student last name) to match.
     * @param searchPrimary Flag indicating whether to search in the primary cohort or all cohorts.
     * @return A list of cohorts matching the search term.
     */
    @Override
    public List<Cohort> search(String term, boolean searchPrimary) {
        if (searchPrimary) {
            return cohortsRepository.findAllByStudentLastNamePrimary(term);
        } else {
            return cohortsRepository.findAllByStudentLastName(term);
        }
    }
}
