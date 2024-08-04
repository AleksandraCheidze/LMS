package de.aittr.lmsbe.repository;

import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CohortsRepository extends JpaRepository<Cohort, Long> {

    Optional<Cohort> findByAlias(String alias);

    Optional<Cohort> findById(Long id);

    @Query("SELECT c FROM Cohort c where c.githubRepository = :cohortRepoName")
    Optional<Cohort> findByGithubRepository(String cohortRepoName);

    @Query("SELECT c FROM Cohort c where c.alias like %:alias")
    Optional<Cohort> findCohortByAlias(String alias);

    @Query("SELECT c FROM  Cohort c WHERE c.id IN :cohortIds")
    Set<Cohort> findByIdIn(List<Long> cohortIds);

    @Query("SELECT u.cohorts FROM User u WHERE u = :user")
    Set<Cohort> findCohortsByUser(@Param("user") User user);

    @Query("SELECT u.cohorts FROM User u WHERE u.email LIKE concat('%', ?1, '%')")
    List<Cohort> findByStudentEmail(String email);

    @Query("SELECT u.primaryCohort FROM User u WHERE u.email LIKE concat('%', ?1, '%')")
    List<Cohort> findByStudentEmailPrimary(String email);

    @Query("SELECT u.cohorts FROM User u WHERE u.firstName LIKE concat('%', ?1, '%')")
    List<Cohort> findAllByStudentFirstName(String firstName);

    @Query("SELECT u.primaryCohort FROM User u WHERE u.firstName LIKE concat('%', ?1, '%')")
    List<Cohort> findAllByStudentFirstNamePrimary(String studentEmailLastName);

    @Query("SELECT u.cohorts FROM User u WHERE u.lastName LIKE concat('%', ?1, '%')")
    List<Cohort> findAllByStudentLastName(String lastName);

    @Query("SELECT u.primaryCohort FROM User u WHERE u.lastName LIKE concat('%', ?1, '%')")
    List<Cohort> findAllByStudentLastNamePrimary(String studentEmailLastName);

    @Query("SELECT u.cohorts FROM User u WHERE " +
            "u.email LIKE concat('%', ?1, '%') " +
            "or u.lastName LIKE concat('%', ?1, '%') " +
            "or u.firstName LIKE concat('%', ?1, '%') ")
    List<Cohort> findByStudentFirstOrLastNameOrEmail(String term);

    @Query("SELECT u.primaryCohort FROM User u WHERE " +
            "u.email LIKE concat('%', ?1, '%') " +
            "or u.lastName LIKE concat('%', ?1, '%') " +
            "or u.firstName LIKE concat('%', ?1, '%') ")
    List<Cohort> findByStudentFirstOrLastNameOrEmailPrimary(String term);

    @Query("SELECT distinct l.cohort FROM Lesson l " +
            "WHERE l.teacher = :selectedTeacher")
    List<Cohort> findAllByTeacher(User selectedTeacher);

    boolean existsByAlias(String alias);

    boolean existsByGithubRepository(String githubRepository);
}
