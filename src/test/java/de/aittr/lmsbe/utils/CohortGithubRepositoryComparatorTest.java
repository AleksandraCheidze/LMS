package de.aittr.lmsbe.utils;

import de.aittr.lmsbe.model.Cohort;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CohortGithubRepositoryComparatorTest {

    private final CohortGithubRepositoryComparator comparator = new CohortGithubRepositoryComparator();


    @Test
    public void testCohortSortingWithStream() {
        Set<Cohort> cohorts = new HashSet<>();
        cohorts.add(getCohort("cohort_34.1"));
        cohorts.add(getCohort("cohort_20"));
        cohorts.add(getCohort("cohort_34"));
        cohorts.add(getCohort("cohort_28"));
        cohorts.add(getCohort("cohort_34.3"));
        cohorts.add(getCohort("cohort_35"));

        Set<Cohort> sortedCohorts = cohorts.stream()
                .sorted(comparator)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<String> sortedRepositoryNames = sortedCohorts.stream()
                .map(Cohort::getGithubRepository)
                .collect(Collectors.toList());

        List<String> expectedOrder = Arrays.asList("cohort_20", "cohort_28", "cohort_34", "cohort_34.1", "cohort_34.3", "cohort_35");
        assertEquals(expectedOrder, sortedRepositoryNames);
    }

    private static Cohort getCohort(String githubRepositoryName) {
        Cohort cohort = new Cohort();
        cohort.setGithubRepository(githubRepositoryName);
        return cohort;
    }
}