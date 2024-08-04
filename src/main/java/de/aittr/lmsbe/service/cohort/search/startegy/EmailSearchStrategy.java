package de.aittr.lmsbe.service.cohort.search.startegy;

import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.repository.CohortsRepository;

import java.util.List;

/**
 * Implementation of the CohortSearchStrategy interface for searching cohorts by student email.
 */
public class EmailSearchStrategy implements CohortSearchStrategy {

    private final CohortsRepository cohortsRepository;

    /**
     * Constructs an EmailSearchStrategy with the provided CohortsRepository.
     *
     * @param cohortsRepository The repository for accessing cohort data.
     */
    public EmailSearchStrategy(CohortsRepository cohortsRepository) {
        this.cohortsRepository = cohortsRepository;
    }

    /**
     * Searches for cohorts based on the student email.
     *
     * @param term          The search term (student email) to match.
     * @param searchPrimary Flag indicating whether to search in the primary cohort or all cohorts.
     * @return A list of cohorts matching the search term.
     */
    @Override
    public List<Cohort> search(String term, boolean searchPrimary) {
        if (searchPrimary) {
            return cohortsRepository.findByStudentEmailPrimary(term);
        } else {
            return cohortsRepository.findByStudentEmail(term);
        }
    }
}
