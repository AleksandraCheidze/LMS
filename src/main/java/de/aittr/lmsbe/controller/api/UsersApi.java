package de.aittr.lmsbe.controller.api;

import de.aittr.lmsbe.dto.*;
import de.aittr.lmsbe.dto.cohort.CohortsPage;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import de.aittr.lmsbe.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tags(value = {
        @Tag(name = "Users")
})
@RequestMapping("/users")
public interface UsersApi {

    /**
     * Registers a new user. Only ADMIN is allowed to perform this operation.
     *
     * @param newUser the new user data
     * @return the created user
     */
    @Operation(summary = "Add/Register User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User is created",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "409", description = "Conflict error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto registerUser(@Parameter(required = true, description = "User")
                         @RequestBody @Valid NewUserDto newUser,
                         @Parameter(hidden = true)
                         @AuthenticationPrincipal AuthenticatedUser currentUser);

    /**
     * Retrieves all cohorts for the authenticated user with confirmed state.
     *
     * @param currentUser the authenticated user
     * @return all user cohorts
     */
    @Operation(summary = "Get all cohorts by user", description = "Only authenticated user with confirmed state allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All user cohorts",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CohortsPage.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Permission is forbidden error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @GetMapping("/my/cohorts")
    @PreAuthorize("isAuthenticated()")
    CohortsPage getAllCohortsByUser(@Parameter(hidden = true, description = "current user")
                                    @AuthenticationPrincipal AuthenticatedUser currentUser);

    /**
     * Confirms user registration by UUID.
     *
     * @param uuidDto the UUID data
     * @return the confirmed user
     */
    @Operation(summary = "Confirm User Registration UUID", description = "Confirm user registration by UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registration confirmed",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @PostMapping("/confirm/check")
    UserDto confirmRegistration(@RequestBody @Valid UuidDto uuidDto);

    /**
     * Sets the user's password.
     *
     * @param userId          the user ID
     * @param passwordRequest the password data
     */
    @Operation(summary = "Set User Password", description = "Set user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set password for user",
                    content = {
                            @Content
                    }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
    })
    @PostMapping("/{userId}/password")
    void setPassword(@PathVariable Long userId,
                     @RequestBody @Valid PasswordDto passwordRequest);

    /**
     * Retrieves the profile of the authenticated user.
     *
     * @param currentUser the authenticated user
     * @return the user profile
     */
    @Operation(summary = "Get profile", description = "Only for authenticated user allowed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "User is not authenticated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    UserDto getMyProfile(@Parameter(hidden = true)
                         @AuthenticationPrincipal AuthenticatedUser currentUser);

    /**
     * Initiates password recovery for a user.
     *
     * @param recoveryDto the password recovery data
     */
    @PostMapping("/password-recovery")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Initiate password recovery", description = "Create a password recovery request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Password recovery request created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    void initiatePasswordRecovery(@RequestBody @Valid PasswordRecoveryDto recoveryDto);

    /**
     * Changes the user's password.
     *
     * @param changeDto   the password change data
     * @param currentUser the authenticated user
     * @return the response indicating the password change status
     */
    @Operation(summary = "Change Password", description = "Changing the user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Password changed successfully",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "401", description = "Invalid old password",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Forbidden: Access is denied",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),
    })
    @PostMapping("/password-change")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    StandardResponseDto changePassword(@RequestBody @Valid PasswordChangeDto changeDto,
                                       @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser currentUser);

    /**
     * Retrieves a list of all users. Only accessible by ADMIN.
     *
     * @return The list of users.
     */
    @Operation(summary = "Get list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users list",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "User not authenticated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    })
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    List<UserDto> getAllUsers();

    /**
     * Updates a user's details. Only accessible by the authenticated user.
     *
     * @param userDto The updated user details.
     * @param userId  The ID of the user.
     * @return The updated user.
     */
    @Operation(summary = "Update User", description = "Authenticated user allowed")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User is updated",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "User not authenticated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "User not found error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),

    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{userId}/update-user")
    UserDto updateUser(@RequestBody UpdateUserDto userDto, @PathVariable Long userId);

    /**
     * Updates a user's primary cohort. Only accessible by ADMIN.
     *
     * @param userId          The ID of the user.
     * @param updateCohortDto The cohort details to update.
     * @return The updated user.
     */
    @Operation(summary = "Update users primary cohort", description = "Admin allowed")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User's primary cohort is updated",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "User not authenticated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "User not found error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),

    })
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{userId}/cohort/primary")
    UserDto updatePrimaryCohort(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateCohortDto updateCohortDto);

    /**
     * Updates a user's additional cohorts. Only accessible by ADMIN.
     *
     * @param userId           The ID of the user.
     * @param updateCohortsDto The additional cohorts to update.
     * @return The updated user.
     */
    @Operation(summary = "Update users additional cohorts", description = "Admin allowed")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User's additional cohorts is updated",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "User not authenticated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "User not found error",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))
                    }),

    })
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{userId}/cohort/additional/reset")
    UserDto updateAdditionalCohort(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateCohortsDto updateCohortsDto);

    /**
     * Updates the primary cohort for multiple users. Only accessible by ADMIN.
     *
     * @param cohortId      The ID of the cohort.
     * @param updateUserIds The list of user IDs to update.
     * @return The list of updated students.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/cohort/primary/{cohortId}")
    List<StudentDto> updatePrimaryCohortByUsers(@PathVariable Long cohortId,
                                                @RequestBody @Valid UpdateUsersPrimaryCohort updateUserIds);

    /**
     * Adds an additional cohort for multiple users. Only accessible by ADMIN.
     *
     * @param updateCohortsDto The additional cohorts to add.
     * @return The list of updated students.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/cohort/additional/add")
    List<StudentDto> addAdditionalCohortByUsers(@RequestBody @Valid UpdateUsersAdditionalCohorts updateCohortsDto);

    /**
     * Removes an additional cohort for a user. Only accessible by ADMIN.
     *
     * @param userId   The ID of the user.
     * @param cohortId The ID of the cohort.
     * @return The updated student.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{userId}/cohort/additional/{cohortId}/remove")
    StudentDto removeAdditionalCohortByUser(@PathVariable Long userId,
                                            @PathVariable Long cohortId);

    /**
     * Retrieves a list of all students. Only accessible by ADMIN.
     *
     * @return The list of students.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/students")
    List<StudentDto> getAllStudents();
}
