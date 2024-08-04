package de.aittr.lmsbe.github.service;

import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.service.FileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static de.aittr.lmsbe.model.User.Role.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    Cohort mockGroup;

    @Nested
    class FileAccessTest {

        @Test
        @DisplayName("Is the file allowed when user's role is null")
        void testRoleIsNull() {
            User nullRoleUser = new User();
            boolean result = fileService.isFileAllowedForUser(nullRoleUser, mockGroup);
            assertFalse(result);
        }

        @Test
        @DisplayName("Is the file not allowed when user's role is null and cohort is not present")
        void testRoleIsNullAndCohortNotPresent() {
            User nullRoleUser = new User();
            Cohort nonExistingGroup = new Cohort();
            nonExistingGroup.setId(999L);
            boolean result = fileService.isFileAllowedForUser(nullRoleUser, nonExistingGroup);
            assertFalse(result);
        }

        @Test
        @DisplayName("Is the file allowed when user's role is null and cohort is present")
        void testRoleIsNullAndCohortPresent() {
            User nullRoleUser = new User();
            Cohort existingGroup = new Cohort();
            existingGroup.setId(999L);
            nullRoleUser.setCohorts(Set.of(existingGroup));
            boolean result = fileService.isFileAllowedForUser(nullRoleUser, existingGroup);
            assertFalse(result);
        }

        @Test
        @DisplayName("Is the file allowed for a user with admin role")
        void testIsFileAllowedForUserWithAdminRole() {
            User adminUser = new User();
            adminUser.setRole(ADMIN);

            boolean result = fileService.isFileAllowedForUser(adminUser, mockGroup);
            assertTrue(result);
        }

        @Test
        @DisplayName("Is the file NOT allowed for a user with student role")
        void testIsFileAllowedForUserWithStudentRole() {
            User studentUser = new User();
            studentUser.setRole(STUDENT);

            boolean result = fileService.isFileAllowedForUser(studentUser, mockGroup);
            assertFalse(result);
        }

        @Test
        @DisplayName("Is the file allowed for a user with teacher role")
        void testIsFileAllowedForUserWithTeacherRole() {
            User teacherUser = new User();
            teacherUser.setRole(TEACHER);

            boolean result = fileService.isFileAllowedForUser(teacherUser, mockGroup);
            assertTrue(result);
        }

        @Test
        @DisplayName("Is the file NOT allowed for a admin user with associated cohort")
        void testIsFileNotAllowedForAdminUserWithAssociatedCohort() {
            Cohort cohort = new Cohort();
            cohort.setId(999L);
            User user = new User();
            user.setRole(ADMIN);
            user.setCohorts(Set.of(cohort));

            boolean result = fileService.isFileAllowedForUser(user, cohort);
            assertTrue(result, "Irrespective of the associated cohort, admin should have access.");
        }

        @Test
        @DisplayName("Is the file NOT allowed for a teacher user with associated cohort")
        void testIsFileNotAllowedForTeacherUserWithAssociatedCohort() {
            Cohort cohort = new Cohort();
            cohort.setId(999L);
            User user = new User();
            user.setRole(TEACHER);
            user.setCohorts(Set.of(cohort));

            boolean result = fileService.isFileAllowedForUser(user, cohort);
            Assertions.assertTrue(result, "Irrespective of the associated cohort, teacher should have access.");
        }

        @Test
        @DisplayName("Is the file NOT allowed for a user with student role and cohort not present")
        void testIsFileAllowedForUserWithStudentRoleAndCohortNotPresent() {
            Cohort nonExistingGroup = new Cohort();
            nonExistingGroup.setId(999L);
            User studentUser = new User();
            studentUser.setRole(STUDENT);

            boolean result = fileService.isFileAllowedForUser(studentUser, nonExistingGroup);
            assertFalse(result);
        }

        @Test
        @DisplayName("Is the file allowed for a user with student role, primary cohort not present, but associated cohort is present")
        void testIsFileAllowedForUserWithStudentRoleAndPrimaryCohortNotPresentAssociatedCohortPresent() {
            Cohort additionalCohort = new Cohort();
            additionalCohort.setId(999L);
            User studentUser = new User();
            studentUser.setRole(STUDENT);
            studentUser.setCohorts(Set.of(additionalCohort));

            boolean result = fileService.isFileAllowedForUser(studentUser, additionalCohort);
            assertTrue(result);
        }

        @Test
        @DisplayName("Is the file allowed for a user with student role and cohort present")
        void testIsFileAllowedForUserWithStudentRoleAndCohortPresent() {
            Cohort additionalCohort = new Cohort();
            additionalCohort.setId(999L);
            User studentUser = new User();
            studentUser.setRole(STUDENT);
            studentUser.setCohorts(Set.of(additionalCohort));

            boolean result = fileService.isFileAllowedForUser(studentUser, additionalCohort);
            assertTrue(result);
        }

        @Test
        @DisplayName("Is the file NOT allowed for NO user and cohort present")
        void testIsFileNotAllowedForNoUserAndCohortPresent() {
            Cohort cohort = new Cohort();
            cohort.setId(999L);

            boolean result = fileService.isFileAllowedForUser(null, cohort);
            assertFalse(result);
        }

        @Test
        @DisplayName("Is the file NOT allowed for a student user and NO cohort present")
        void testIsFileNotAllowedForStudentUserAndNoCohortPresent() {
            User studentUser = new User();
            studentUser.setRole(STUDENT);

            boolean result = fileService.isFileAllowedForUser(studentUser, null);
            assertFalse(result);
        }

        @Test
        @DisplayName("Is the file NOT allowed for a student user with additional cohort being the primary one and entering a different cohort")
        void testIsFileNotAllowedForStudentUserWithAdditionalAsPrimaryCohortAndEnteringDifferentCohort() {
            Cohort associatedCohort = new Cohort();
            associatedCohort.setId(999L);
            User studentUser = new User();
            studentUser.setRole(STUDENT);
            studentUser.setCohorts(Set.of(associatedCohort));
            studentUser.setPrimaryCohort(associatedCohort);

            Cohort otherCohort = new Cohort();
            otherCohort.setId(888L);

            boolean result = fileService.isFileAllowedForUser(studentUser, otherCohort);
            assertFalse(result);
        }
    }
}
