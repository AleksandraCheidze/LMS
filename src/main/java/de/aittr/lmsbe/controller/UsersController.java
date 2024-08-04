package de.aittr.lmsbe.controller;

import de.aittr.lmsbe.controller.api.UsersApi;
import de.aittr.lmsbe.dto.*;
import de.aittr.lmsbe.dto.cohort.CohortsPage;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import de.aittr.lmsbe.service.UsersService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class UsersController implements UsersApi {

    UsersService usersService;

    @Override
    public UserDto registerUser(NewUserDto newUser, AuthenticatedUser currentUser) {
        User user = currentUser == null ? null : currentUser.getUser();
        return usersService.registerUser(newUser, user);
    }

    @Override
    public CohortsPage getAllCohortsByUser(AuthenticatedUser currentUser) {
        Long userId = currentUser.getUser().getId();
        return usersService.getAllCohortsByUser(userId);
    }

    @Override
    public UserDto confirmRegistration(UuidDto uuidDto) {
        return usersService.confirmRegistration(uuidDto);
    }

    @Override
    public void setPassword(Long userId, PasswordDto passwordRequest) {
        usersService.setPassword(userId, passwordRequest);
    }

    @Override
    public UserDto getMyProfile(AuthenticatedUser currentUser) {
        Long userId = currentUser.getUser().getId();
        return usersService.getUser(userId);
    }

    @Override
    public void initiatePasswordRecovery(PasswordRecoveryDto recoveryDto) {
        usersService.initiatePasswordRecovery(recoveryDto);
    }

    @Override
    public StandardResponseDto changePassword(PasswordChangeDto changeDto, AuthenticatedUser currentUser) {
        Long userId = currentUser.getUser().getId();
        return usersService.changePassword(changeDto, userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return usersService.getAllUsers();
    }

    @Override
    public UserDto updateUser(UpdateUserDto newUserDto, Long userId) {
        return usersService.updateUser(newUserDto, userId);
    }

    @Override
    public UserDto updatePrimaryCohort(Long userId, UpdateCohortDto updateCohortDto) {
        return usersService.updatePrimaryCohort(userId, updateCohortDto);
    }

    @Override
    public UserDto updateAdditionalCohort(Long userId, UpdateCohortsDto updateCohortsDto) {
        return usersService.updateAdditionalCohort(userId, updateCohortsDto);
    }

    @Override
    public List<StudentDto> updatePrimaryCohortByUsers(Long cohortId, UpdateUsersPrimaryCohort updateUserIds) {
        return usersService.updatePrimaryCohortByUsers(cohortId, updateUserIds.getUserIds());
    }

    @Override
    public List<StudentDto> addAdditionalCohortByUsers(UpdateUsersAdditionalCohorts updateCohortsDto) {
        return usersService.addAdditionalCohortsByUsers(updateCohortsDto);
    }

    @Override
    public StudentDto removeAdditionalCohortByUser(Long userId, Long cohortId) {
        return usersService.removeAdditionalCohortsByUser(userId, cohortId);
    }

    @Override
    public List<StudentDto> getAllStudents() {
        return usersService.getAllStudents();
    }
}
