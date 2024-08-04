package de.aittr.lmsbe.service.file_rules;

import de.aittr.lmsbe.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrej Reutow
 * created on 21.02.2024
 */
@DisplayName("Testing HighLevelRoleRule class")
class HighLevelRoleRuleTest {
    HighLevelRoleRule highLevelRoleRule;

    @BeforeEach
    void setup() {
        highLevelRoleRule = new HighLevelRoleRule();
    }

    @Test
    @DisplayName("Should return true for TEACHER Role")
    void testHighRoleTeacher() {
        User user = new User();
        user.setRole(User.Role.TEACHER);

        boolean result = highLevelRoleRule.test(user);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false for STUDENT Role")
    void testLowRoleStudent() {
        User user = new User();
        user.setRole(User.Role.STUDENT);

        boolean result = highLevelRoleRule.test(user);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true for ADMIN Role")
    void testAdminRoleReturnTrue() {
        User user = new User();
        user.setRole(User.Role.ADMIN);

        boolean result = highLevelRoleRule.test(user);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false for No Role")
    void testNoRoleReturnFalse() {
        User user = new User();

        boolean result = highLevelRoleRule.test(user);
        assertFalse(result);
    }
}
