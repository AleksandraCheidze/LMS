package de.aittr.lmsbe.github.uttils;

import de.aittr.lmsbe.model.User;

/**
 * The GHNameGenerator class provides utility methods for generating branch names for users.
 */
public class GHNameGenerator {
    private GHNameGenerator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a branch name for a user in a specific format.
     *
     * @param currentUser the current user object
     * @return the generated branch name
     */
    public static String generateHwBranchName(final User currentUser) {
        String userFullName = mapToUserFullName(currentUser);
        String userEmail = currentUser.getEmail();
        return (userFullName + "-" + userEmail).replace(" ", "_");
    }

    /**
     * Maps the first and last name of a User object to the full name.
     *
     * @param currentUser the User object whose full name needs to be mapped
     * @return the full name of the User object
     */
    public static String mapToUserFullName(final User currentUser) {
        return currentUser.getFirstName().trim() + " " + currentUser.getLastName().trim();
    }
}
