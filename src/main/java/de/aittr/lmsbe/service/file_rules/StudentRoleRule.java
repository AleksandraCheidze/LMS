package de.aittr.lmsbe.service.file_rules;

import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static de.aittr.lmsbe.model.User.Role.STUDENT;

@Slf4j
public class StudentRoleRule {

    /**
     * This method checks if a given user belongs to a specific cohort.
     *
     * @param user The user to check.
     * @param cohort The cohort to check against.
     * @return {@code true} if the user belongs to the cohort, {@code false} otherwise.
     */
    public boolean test(@NonNull final User user, @NonNull final Cohort cohort) {
        Set<Cohort> studentCohorts = user.getCohorts();
        Cohort primaryCohort = user.getPrimaryCohort();
        final String logPrefix = String.format("User: %s, Role: %s, Cohort: %s - ", user.getEmail(), user.getRole(), cohort.getName());
        log.debug(logPrefix + "Primary cohort: {}, Additional studentCohorts: {}",
                primaryCohort, studentCohorts);
        return STUDENT.equals(user.getRole()) && (cohort.equals(primaryCohort) || (studentCohorts != null && studentCohorts.contains(cohort)));
    }

}
