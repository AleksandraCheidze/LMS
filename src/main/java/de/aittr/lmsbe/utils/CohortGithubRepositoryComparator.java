package de.aittr.lmsbe.utils;

import de.aittr.lmsbe.model.Cohort;

import java.util.Comparator;

public class CohortGithubRepositoryComparator implements Comparator<Cohort> {

    @Override
    public int compare(Cohort cohort1, Cohort cohort2) {
        final String githubRepo1 = cohort1.getGithubRepository();
        final String githubRepo2 = cohort2.getGithubRepository();

        if (null == githubRepo1 && null == githubRepo2) {
            return 0;
        } else if (null == githubRepo1) {
            return -1;
        } else if (null == githubRepo2) {
            return 1;
        }

        return githubRepo1.compareTo(githubRepo2);
    }
}