package de.aittr.lmsbe.service.cohort;

import de.aittr.lmsbe.dto.StudentDto;
import de.aittr.lmsbe.dto.cohort.*;
import de.aittr.lmsbe.exception.ConflictException;
import de.aittr.lmsbe.exception.RestException;
import de.aittr.lmsbe.github.service.GHRepositoryService;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.Lesson;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.repository.CohortsRepository;
import de.aittr.lmsbe.service.interfaces.ICohortService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static de.aittr.lmsbe.service.cohort.CohortUtils.calculateCohortStats;
import static de.aittr.lmsbe.service.cohort.CohortUtils.getTeacherLessons;

/**
 * @author Andrej Reutow
 * created on 11.11.2023
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CohortService implements ICohortService {


    @Value("${git.template-owner}")
    private String templateOwner;

    private final CohortsRepository cohortsRepository;
    private final GHRepositoryService ghRepositoryService;


    @Override
    public List<CohortDto> getAll() {
        log.debug("Get all cohorts at work");
        return CohortDto.from(cohortsRepository.findAll()).stream()
                .sorted(Comparator.comparing(CohortDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentCohortDto> getAllStudentsCohort() {
        List<CohortStats> result = new ArrayList<>();
        calculateCohortStats(cohortsRepository.findAll(), result);
        return result.stream()
                .map(StudentCohortDto::from)
                .sorted(Comparator.comparing(StudentCohortDto::getCohortAlias))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherCohortDto> getAllTeachersCohort(@NonNull  User currentUser,
                                                       @NonNull  LocalDateTime from,
                                                       @NonNull  LocalDateTime to) {
        log.debug("Getting all cohorts for given teacher: {}", currentUser);
        List<Cohort> allByTeacher = cohortsRepository.findAllByTeacher(currentUser);
        log.debug("Processing {} cohorts to DTOs", allByTeacher.size());
        return allByTeacher
                .stream()
                .map(cohort -> toTeacherCohortDto(cohort, currentUser, from, to))
                .collect(Collectors.toList());
    }

    /**
     * Converts a single Cohort object into a TeacherCohortDto object by extracting necessary data.
     *
     * @param cohort      The Cohort object to convert.
     * @param currentUser The current User object.
     * @param from        The start LocalDateTime.
     * @param to          The end LocalDateTime.
     * @return A {@link TeacherCohortDto} object containing the mapped data from the specified Cohort.
     * @throws NullPointerException if any of the parameters is null.
     */
    private TeacherCohortDto toTeacherCohortDto(Cohort cohort, User currentUser, LocalDateTime from, LocalDateTime to) {
        log.debug("Starting transformation of Cohort into TeacherCohortDto");
        List<Lesson> teacherLessons = getTeacherLessons(currentUser, from, to, cohort);
        TeacherCohortDto teacherCohortDto = new TeacherCohortDto(
                cohort.getId(),
                cohort.getAlias(),
                teacherLessons.size());
        log.debug("Completed transformation of Cohort into TeacherCohortDto");
        return teacherCohortDto;
    }

    @Transactional
    @Override
    public Cohort getCohortByAliasOrNull(String cohortAlias) {
        log.debug("Find cohort for alias: {}", cohortAlias);
        return cohortsRepository.findCohortByAlias(cohortAlias).orElse(null);
    }

    @Transactional
    @Override
    public Cohort getCohortByAliasOrThrow(String cohortAlias) {
        log.debug("Find cohort for alias: {}", cohortAlias);
        return cohortsRepository.findCohortByAlias(cohortAlias)
                .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, "Cohort " + cohortAlias + " not exists"));
    }

    @Transactional
    @Override
    public Cohort getCohortByRepoNameOrThrow(@NonNull String cohortRepoName) {
        log.debug("Find cohort for repository name: {}", cohortRepoName);
        return cohortsRepository.findByGithubRepository(cohortRepoName)
                .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, "Cohort " + cohortRepoName + " not exists"));
    }

    @Transactional
    @Override
    public Set<Cohort> findByIdIn(List<Long> cohortIds) {
        return cohortsRepository.findByIdIn(cohortIds);
    }


    @Override
    public Cohort getCohortByIdOrThrow(Long id) {
        return cohortsRepository.findById(id)
                .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, "Cohort by ID: '" + id + "' not exists"));
    }

    @Transactional
    @Override
    public CohortStudentDto getStudentByCohortId(long cohortId) {
        final Cohort selectedCohort = getCohortByIdOrThrow(cohortId);
        return new CohortStudentDto(
                CohortDto.from(selectedCohort),
                selectedCohort.getUsers()
                        .stream()
                        .map(StudentDto::from)
                        .sorted(Comparator.comparing(StudentDto::getStudentId))
                        .collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public Cohort save(Cohort cohort) {
        return cohortsRepository.save(cohort);
    }

    @Override
    public boolean checkUserCohorts(User user, Long cohortId) {
        Cohort cohort = getCohortByIdOrThrow(cohortId);
        return user.getPrimaryCohort().equals(cohort) || user.getCohorts().contains(cohort);
    }

    @Transactional
    public CohortRepoDto createCohort(@Valid CohortDto cohortDto) {
        if (cohortsRepository.existsByAlias(cohortDto.getAlias())) {
            throw new ConflictException("Cohort with alias " + cohortDto.getAlias() + " already exists");
        }

        GHRepository newRepo = ghRepositoryService.createRepositoryFromTemplate(cohortDto.getGithubRepository(), true);
        Cohort cohort = new Cohort();
        cohort.setName(cohortDto.getName());
        cohort.setAlias(cohortDto.getAlias());
        cohort.setGithubRepository(newRepo.getName());
        Cohort savedCohort = cohortsRepository.save(cohort);
        log.debug("Created a new cohort: {}", savedCohort);
        String repoUrl = newRepo.getHtmlUrl().toString();

        return CohortRepoDto.from(savedCohort, repoUrl);
    }


    @Transactional
    public CohortRepoDto updateCohort(Long id, @Valid CohortDto updatedCohortDto) {
    Cohort cohort = getCohortByIdOrThrow(id);

    if (!cohort.getAlias().equals(updatedCohortDto.getAlias()) &&
            cohortsRepository.existsByAlias(updatedCohortDto.getAlias())) {
        throw new ConflictException("Cohort with alias " + updatedCohortDto.getAlias() + " already exists");
    }

    String currentRepoName = cohort.getGithubRepository();
    String newRepoName = updatedCohortDto.getGithubRepository();
    if (!currentRepoName.equals(newRepoName)) {
        try {
            GHRepository updatedRepo = ghRepositoryService.updateCohortVersion(currentRepoName, newRepoName);
            updatedRepo.getHtmlUrl();
            cohort.setGithubRepository(newRepoName);
        } catch (ConflictException e) {
            throw new ConflictException("Failed to rename repository: " + e.getMessage());
        }
    }
    cohort.setName(updatedCohortDto.getName());
    cohort.setAlias(updatedCohortDto.getAlias());
    Cohort updatedCohort = cohortsRepository.save(cohort);
    log.debug("Updated cohort: {}", updatedCohort);

    GHRepository updatedRepo = ghRepositoryService.getRepository(templateOwner, cohort.getGithubRepository());
    String repoUrl = updatedRepo.getHtmlUrl().toString();

    return CohortRepoDto.from(updatedCohort, repoUrl);
}
}


