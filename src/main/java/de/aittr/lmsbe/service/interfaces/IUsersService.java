package de.aittr.lmsbe.service.interfaces;

import de.aittr.lmsbe.dto.*;
import de.aittr.lmsbe.dto.cohort.CohortsPage;
import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.exception.ConflictException;
import de.aittr.lmsbe.exception.NotFoundException;
import de.aittr.lmsbe.exception.UnauthorizedException;
import de.aittr.lmsbe.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public interface IUsersService {

    /**
     * Adds a new user to the system.
     *
     * @param newUser The DTO containing new user details.
     * @param user
     * @return The created user as a DTO.
     * @throws ConflictException If the email is already registered.
     * @throws NotFoundException If the specified role is invalid.
     */
    UserDto registerUser(NewUserDto registerUserDto, User authUser);

    /**
     * Retrieves all cohorts associated with a user.
     *
     * @param userId The ID of the user.
     * @return A page containing cohort details.
     */
    CohortsPage getAllCohortsByUser(Long userId);

    /**
     * Confirms user registration using a UUID.
     *
     * @param uuidDto The DTO containing the UUID.
     * @return The confirmed user as a DTO.
     * @throws BadRequestException If the UUID is invalid or expired.
     */
    UserDto confirmRegistration(UuidDto uuidDto);

    /**
     * Sets a new password for the user.
     *
     * @param userId          The ID of the user.
     * @param passwordRequest The DTO containing the new password details.
     * @throws ConflictException If the user is attempting to set the password for another user.
     */
    void setPassword(Long userId, PasswordDto passwordRequest);

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user.
     * @return The user as a DTO.
     * @throws NotFoundException If the user is not found.
     */
    UserDto getUser(Long userId);

    /**
     * Retrieves a user by their ID or throws a NotFoundException if the user is not found.
     *
     * @param userId The ID of the user to retrieve.
     * @return The user entity.
     * @throws NotFoundException If the user with the specified ID is not found.
     */
    User getUserOrThrow(Long userId);

    /**
     * Initiates the password recovery process for a user.
     *
     * @param recoveryDto The DTO containing the user's email for password recovery.
     * @throws NotFoundException If the user with the specified email is not found.
     */
    void initiatePasswordRecovery(PasswordRecoveryDto recoveryDto);

    /**
     * Changes the password for a user.
     *
     * @param changeDto The DTO containing old and new password details.
     * @param userId    The ID of the user.
     * @return A standard response indicating the result of the operation.
     * @throws UnauthorizedException If the old password is incorrect.
     */
    StandardResponseDto changePassword(PasswordChangeDto changeDto, Long userId);

    /**
     * Retrieves all users in the system.
     *
     * @return A list of user DTOs.
     */
    List<UserDto> getAllUsers();

    /**
     * Retrieves a user by their email.
     *
     * @param email The email of the user.
     * @return The user.
     * @throws NotFoundException If the user with the specified email is not found.
     */
    User getUserByEmailOrThrow(String email);

    /**
     * Finds a user by their email.
     *
     * @param email The email of the user.
     * @return An optional containing the user, if found.
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Finds a user by their Zoom email.
     *
     * @param email The Zoom email of the user.
     * @return An optional containing the user, if found.
     */
    Optional<User> findUserByZoomEmail(String email);

    /**
     * Updates a user with new details.
     *
     * @param newUserDto The DTO containing updated user details.
     * @param userId     The ID of the user to update.
     * @return The updated user as a DTO.
     * @throws BadRequestException If the authenticated user is not allowed to update the specified user.
     */
    UserDto updateUser(UpdateUserDto newUserDto, Long userId);

    /**
     * Updates the primary cohort for a user.
     *
     * @param userId          The ID of the user.
     * @param updateCohortDto The DTO containing the new cohort details.
     * @return The updated user as a DTO.
     */
    UserDto updatePrimaryCohort(Long userId, UpdateCohortDto updateCohortDto);

    /**
     * Updates the additional cohorts for a user.
     *
     * @param userId           The ID of the user.
     * @param updateCohortsDto The DTO containing the new cohort details.
     * @return The updated user as a DTO.
     */
    UserDto updateAdditionalCohort(Long userId, UpdateCohortsDto updateCohortsDto);

    /**
     * Checks if a user with the specified email exists.
     *
     * @param email The email to check.
     * @return True if a user with the email exists, false otherwise.
     */
    boolean isUserWithEmailExists(String email);

    /**
     * Adds additional cohorts to multiple users.
     *
     * @param updateCohortsDto The DTO containing user IDs and cohort IDs.
     * @return A list of updated student DTOs.
     */
    @Transactional
    List<StudentDto> addAdditionalCohortsByUsers(UpdateUsersAdditionalCohorts updateCohortsDto);

    /**
     * Removes an additional cohort from a user.
     *
     * @param userId   The ID of the user.
     * @param cohortId The ID of the cohort to remove.
     * @return The updated student DTO.
     */
    @Transactional
    StudentDto removeAdditionalCohortsByUser(Long userId, Long cohortId);

    /**
     * Updates the primary cohort for multiple users.
     *
     * @param cohortId The ID of the new primary cohort.
     * @param userIds  The list of user IDs to update.
     * @return A list of updated student DTOs.
     */
    @Transactional
    List<StudentDto> updatePrimaryCohortByUsers(Long cohortId, List<Long> userIds);

    /**
     * Retrieves all students in the system.
     *
     * @return A list of student DTOs.
     */
    List<StudentDto> getAllStudents();
}
