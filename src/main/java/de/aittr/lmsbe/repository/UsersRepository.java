package de.aittr.lmsbe.repository;

import de.aittr.lmsbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    /**
     * Finds a user in the database based on their email.
     *
     * @param email the email of the user to find
     * @return an Optional User object representing the found user, or an empty Optional if no user is found
     */
    @Query("select u from User u where upper(u.email) = upper(?1)")
    Optional<User> findByEmail(String email);

    boolean existsByZoomAccount(String zoomAccount);

    /**
     * Finds all users in the database with the given role.
     *
     * @param role the role of the users to find
     * @return a List object containing all the users with the given role, or an empty List if no users are found
     */
    @Query("select u from User u where u.role = ?1 order by u.id")
    List<User> findAllByRole(@NonNull User.Role role);

    /**
     * Finds a user in the database based on their Zoom account email.
     *
     * @param zoomAccountEmail the email associated with the Zoom account
     * @return an Optional User object representing the found user, or an empty Optional if no user is found
     */
    @Query("select u from User u where upper(u.zoomAccount) = upper(?1)")
    Optional<User> findByZoomEmail(String zoomAccountEmail);
}
