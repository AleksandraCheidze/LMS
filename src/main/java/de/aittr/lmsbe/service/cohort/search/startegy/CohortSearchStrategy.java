package de.aittr.lmsbe.service.cohort.search.startegy;

import de.aittr.lmsbe.model.Cohort;

import java.util.List;

public interface CohortSearchStrategy {
    List<Cohort> search(String term, boolean searchPrimary);
}
