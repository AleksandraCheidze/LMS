package de.aittr.lmsbe.github.uttils;

import de.aittr.lmsbe.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andrej Reutow
 * created on 01.04.2024
 */
class GHNameGeneratorTest {


    @Test
    void generateHwBranchNameTest() {
        User testUser = new User();
        testUser.setEmail("test@email.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        String branchName = GHNameGenerator.generateHwBranchName(testUser);

        assertThat(branchName).isEqualTo("John_Doe-test@email.com");
    }

    @Test
    void testMapToUserFullName() {

        User testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        String result = GHNameGenerator.mapToUserFullName(testUser);

        assertEquals("John Doe", result, "The mapToUserFullName method did not return the expected result.");
    }
}
