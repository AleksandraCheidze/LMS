package de.aittr.lmsbe.service.file_rules;

import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static de.aittr.lmsbe.model.User.Role.STUDENT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrej Reutow
 * created on 21.02.2024
 */
@DisplayName("Testing StudentRoleRule class")
class StudentRoleRuleTest {

    User testUser;
    Cohort testCohort;
    Cohort additionalCohort;
    StudentRoleRule studentRoleRule;
    Set<Cohort> studentCohorts;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setEmail("test.email@example.com");
        testUser.setRole(STUDENT);

        testCohort = new Cohort();
        testCohort.setName("Test Cohort");

        additionalCohort = new Cohort();
        additionalCohort.setName("Additional Cohort");

        studentCohorts = new HashSet<>();
        studentCohorts.add(additionalCohort);

        testUser.setCohorts(studentCohorts);
        testUser.setPrimaryCohort(testCohort);

        studentRoleRule = new StudentRoleRule();
    }

    @Test
    @DisplayName("Test when cohort equals primary cohort")
    void testCohortEqualsPrimary() {
        boolean belongsToCohortPrimary = studentRoleRule.test(testUser, testCohort);
        assertTrue(belongsToCohortPrimary);
    }

    @Test
    @DisplayName("Test when cohort is in the list of additional cohorts")
    void testCohortInAdditionalCohorts() {
        boolean belongsToCohortAdditional = studentRoleRule.test(testUser, additionalCohort);
        assertTrue(belongsToCohortAdditional);
    }

    @Test
    @DisplayName("Test when cohort is not associated with the user")
    void testCohortNotAssociatedWithUser() {
        boolean notBelongsCohort = studentRoleRule.test(testUser, new Cohort());
        assertFalse(notBelongsCohort);
    }
}
