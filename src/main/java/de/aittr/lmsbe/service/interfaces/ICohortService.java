package de.aittr.lmsbe.service.interfaces;


import de.aittr.lmsbe.dto.cohort.CohortDto;
import de.aittr.lmsbe.dto.cohort.CohortStudentDto;
import de.aittr.lmsbe.dto.cohort.StudentCohortDto;
import de.aittr.lmsbe.dto.cohort.TeacherCohortDto;
import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public interface ICohortService {

    /**
     * Retrieves all cohorts and converts them to DTOs.
     *
     * @return a list of CohortDto objects.
     */
    List<CohortDto> getAll();

    /**
     * Retrieves all student cohorts and converts them to DTOs.
     *
     * @return a list of StudentCohortDto objects.
     */
    List<StudentCohortDto> getAllStudentsCohort();

    /**
     * Retrieves all cohorts for a given teacher and maps them to DTOs using Java Streams API.
     *
     * @param currentUser The User object representing the teacher.
     * @param from        The starting date and time to filter the cohorts.
     * @param to          The ending date and time to filter the cohorts.
     * @return A Stream-derived list of {@link TeacherCohortDto} objects, each representing a mapped Cohort object.
     * @throws NullPointerException     if any of the parameters is null.
     * @throws IllegalArgumentException if the 'from' date is after the 'to' date.
     */
    List<TeacherCohortDto> getAllTeachersCohort(final User currentUser,
                                                final LocalDateTime from,
                                                final LocalDateTime to);

    /**
     * Retrieves a cohort by its alias or returns null if not found.
     *
     * @param cohortAlias The alias of the cohort.
     * @return the Cohort object or null.
     */
    Cohort getCohortByAliasOrNull(String cohortAlias);

    /**
     * Retrieves a cohort by its alias or throws an exception if not found.
     *
     * @param cohortAlias The alias of the cohort.
     * @return the Cohort object.
     * @throws RestException if the cohort is not found.
     */
    Cohort getCohortByAliasOrThrow(String cohortAlias);

    /**
     * Retrieves a cohort by its GitHub repository name or throws an exception if not found.
     *
     * @param cohortRepoName The repository name of the cohort.
     * @return the Cohort object.
     * @throws RestException if the cohort is not found.
     */
    Cohort getCohortByRepoNameOrThrow(String cohortRepoName);

    /**
     * Retrieves cohorts by their IDs.
     *
     * @param cohortIds The list of cohort IDs.
     * @return a set of Cohort objects.
     */
    Set<Cohort> findByIdIn(List<Long> cohortIds);

    /**
     * Retrieves a cohort by its ID or throws an exception if not found.
     *
     * @param id The ID of the cohort.
     * @return the Cohort object.
     * @throws RestException if the cohort is not found.
     */
    Cohort getCohortByIdOrThrow(Long id);

    /**
     * Retrieves students of a cohort by its ID.
     *
     * @param cohortId The ID of the cohort.
     * @return a CohortStudentDto object.
     * @throws RestException if the cohort is not found.
     */
    CohortStudentDto getStudentByCohortId(long cohortId);

    /**
     * Saves a cohort.
     *
     * @param cohort The cohort to save.
     * @return the saved Cohort object.
     */
    Cohort save(Cohort cohort);

    /**
     * Checks if a user is associated with a given cohort.
     *
     * @param user     The user.
     * @param cohortId The ID of the cohort.
     * @return true if the user is associated with the cohort, false otherwise.
     */
    boolean checkUserCohorts(User user, Long cohortId);
}
