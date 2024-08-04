package de.aittr.lmsbe.service.cohort.search;

import de.aittr.lmsbe.dto.cohort.CohortStats;
import de.aittr.lmsbe.dto.cohort.StudentCohortDto;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.repository.CohortsRepository;
import de.aittr.lmsbe.service.cohort.search.startegy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.aittr.lmsbe.service.cohort.CohortUtils.calculateCohortStats;

/**
 * Service class responsible for filtering cohorts based on different search strategies.
 */
@Service
public class CohortFilter implements ICohortFilter {

    private final Map<String, CohortSearchStrategy> strategies;

    /**
     * Constructs a CohortFilter instance.
     *
     * @param cohortsRepository The repository for accessing cohorts data.
     */
    @Autowired
    public CohortFilter(CohortsRepository cohortsRepository) {
        this.strategies = new HashMap<>();
        this.strategies.put("email", new EmailSearchStrategy(cohortsRepository));
        this.strategies.put("firstName", new FirstNameSearchStrategy(cohortsRepository));
        this.strategies.put("lastName", new LastNameSearchStrategy(cohortsRepository));
        this.strategies.put("default", new FullnameSearchStrategy(cohortsRepository));
    }


    @Override
    public List<StudentCohortDto> filterByScope(String scope, String term, boolean searchPrimary) {
        List<CohortStats> result = new ArrayList<>();
        CohortSearchStrategy strategy = strategies.get(scope);
        if (strategy == null) {
            strategy = strategies.get("default");
        }
        List<Cohort> cohorts = strategy.search(term, searchPrimary);
        calculateCohortStats(cohorts, result);
        return result
                .stream()
                .map(StudentCohortDto::from)
                .collect(Collectors.toList());
    }
}
