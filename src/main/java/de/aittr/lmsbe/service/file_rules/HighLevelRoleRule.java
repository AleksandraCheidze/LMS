package de.aittr.lmsbe.service.file_rules;

import de.aittr.lmsbe.model.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static de.aittr.lmsbe.model.User.Role.ADMIN;
import static de.aittr.lmsbe.model.User.Role.TEACHER;

@Slf4j
public class HighLevelRoleRule {

    /**
     * Checks if a user has a high level role.
     *
     * @param user The user to check. Must not be null.
     * @return True if the user has a high level role, false otherwise.
     */
    public boolean test(@NonNull final User user) {
        User.Role userRole = user.getRole();
        if (TEACHER.equals(userRole) || ADMIN.equals(userRole)) {
            log.debug("Permission granted for user {}, (user has high level role).", user.getEmail());
            return true;
        }
        return false;
    }
}
