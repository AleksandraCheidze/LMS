package de.aittr.lmsbe.service;

import de.aittr.lmsbe.dto.*;
import de.aittr.lmsbe.dto.cohort.CohortDto;
import de.aittr.lmsbe.dto.cohort.CohortsPage;
import de.aittr.lmsbe.exception.*;
import de.aittr.lmsbe.mail.service.EmailService;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.model.UserConfirmationCode;
import de.aittr.lmsbe.repository.AccountsConfirmRepository;
import de.aittr.lmsbe.repository.CohortsRepository;
import de.aittr.lmsbe.repository.UsersRepository;
import de.aittr.lmsbe.service.cohort.CohortService;
import de.aittr.lmsbe.service.interfaces.IUsersService;
import de.aittr.lmsbe.utils.CohortGithubRepositoryComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.aittr.lmsbe.dto.UserDto.from;
import static de.aittr.lmsbe.model.User.Role;
import static de.aittr.lmsbe.model.User.State.CONFIRMED;
import static de.aittr.lmsbe.model.User.State.NOT_CONFIRMED;

@RequiredArgsConstructor
@Service
@Slf4j
public class UsersService implements IUsersService {

    private final UsersRepository usersRepository;
    private final CohortsRepository cohortsRepository;
    private final AccountsConfirmRepository accountsConfirmRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CohortService cohortService;

    @Value("${confirm-link-expired-days}")
    int confirmLinkExpiredDays;

    @Transactional
    @Override
    public UserDto registerUser(NewUserDto registerUserDto, User authUser) {
        validateRegisterUser(registerUserDto);
        final Role userRole = determineRole(registerUserDto, authUser);
        final Cohort cohort = getCohortForStudent(userRole, registerUserDto);

        User user = User.builder()
                .email(registerUserDto.getEmail())
                .role(userRole)
                .state(NOT_CONFIRMED)
                .firstName(registerUserDto.getFirstName().trim())
                .lastName(registerUserDto.getLastName().trim())
                .country(registerUserDto.getCountry())
                .phone(registerUserDto.getPhone())
                .isActive(true)
                .primaryCohort(cohort)
                .cohorts(new HashSet<>())
                .csvFiles(new HashSet<>())
                .build();

        if (cohort != null) {
            user.getCohorts().add(cohort);
        }

        usersRepository.save(user);
        log.debug("{} is created in DB", user.getEmail());

        UserConfirmationCode userConfirmationCode = UserConfirmationCode.builder()
                .user(user)
                .uuid(UUID.randomUUID().toString())
                .expiredTime(LocalDateTime.now().plusDays(confirmLinkExpiredDays))
                .build();

        accountsConfirmRepository.save(userConfirmationCode);
        emailService.sendRegistrationEmail(userConfirmationCode);

        log.info("Registration email has been sent to {}", userConfirmationCode.getUser().getEmail());
        return from(user);
    }

    private void validateRegisterUser(NewUserDto registerUserDto) {
        if (usersRepository.existsByEmail(registerUserDto.getEmail())) {
            log.error("{} is already registered", registerUserDto.getEmail());
            throw new ConflictException("This email is already registered");
        }
        if (registerUserDto.getCountry() != null) {
            registerUserDto.setCountry(registerUserDto.getCountry().trim());
        }
    }

    private Role determineRole(NewUserDto registerUserDto, User authUser) {
        if (authUser == null || !Role.ADMIN.equals(authUser.getRole())) {
            registerUserDto.setRole("STUDENT");
        }
        return Role.findRole(registerUserDto.getRole())
                .orElseThrow(() -> new NotFoundException("ROLE", registerUserDto.getRole()));
    }

    private Cohort getCohortForStudent(Role userRole, NewUserDto registerUserDto) {
        Cohort cohort = null;
        if (userRole.equals(Role.STUDENT)) {
            if (StringUtils.isBlank(registerUserDto.getCohort())) {
                throw new RestException(HttpStatus.BAD_REQUEST, "Cohort can not be empty");
            } else {
                cohort = cohortService.getCohortByAliasOrThrow(registerUserDto.getCohort());
            }
        }
        return cohort;
    }

    @Override
    public CohortsPage getAllCohortsByUser(Long userId) {
        User user = getUserOrThrow(userId);
        List<Cohort> cohorts;
        CohortGithubRepositoryComparator comparator = new CohortGithubRepositoryComparator();
        if (user.getRole().equals(Role.TEACHER) || user.getRole().equals(Role.ADMIN)) {
            cohorts = cohortsRepository.findAll()
                    .stream()
                    .sorted(comparator)
                    .collect(Collectors.toCollection(LinkedList::new));
        } else {
            cohorts = user.getCohorts()
                    .stream()
                    .sorted(comparator)
                    .collect(Collectors.toCollection(LinkedList::new));
        }

        return CohortsPage.builder()
                .cohorts(CohortDto.from(cohorts))
                .build();
    }

    @Transactional
    @Override
    public UserDto confirmRegistration(UuidDto uuidDto) {
        UserConfirmationCode userConfirmationCode = getAccountConfirmByUuidOrThrow(uuidDto.getUuid());
        User user = userConfirmationCode.getUser();

        return from(user);
    }

    @Transactional
    @Override
    public void setPassword(Long userId, PasswordDto passwordRequest) {
        UserConfirmationCode userConfirmationCode = getAccountConfirmByUuidOrThrow(passwordRequest.getUuid());
        User currentUser = getUserOrThrow(userId);
        User user = userConfirmationCode.getUser();

        if (!currentUser.equals(user)) {
            throw new ConflictException("Not allowed set password for another user");
        }

        user.setPasswordHash(passwordEncoder.encode(passwordRequest.getPassword()));
        user.setState(CONFIRMED);
        usersRepository.save(user);
        accountsConfirmRepository.delete(userConfirmationCode);
    }


    @Override
    public UserDto getUser(Long userId) {
        return from(getUserOrThrow(userId));
    }

    @Override
    public User getUserOrThrow(Long userId) {
        return usersRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User", userId));
    }


    /**
     * Retrieves a user confirmation code by UUID or throws a BadRequestException if the UUID is invalid or expired.
     *
     * @param uuid The UUID of the confirmation code.
     * @return The user confirmation code entity.
     * @throws BadRequestException If the UUID is invalid or the confirmation link is expired.
     */
    private UserConfirmationCode getAccountConfirmByUuidOrThrow(String uuid) {
        UserConfirmationCode userConfirmationCode = accountsConfirmRepository.findByUuid(uuid)
                .orElseThrow(() -> new BadRequestException("Invalid UUID"));
        if (userConfirmationCode.getExpiredTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Your confirmation link is expired");
        }
        return userConfirmationCode;
    }


    @Transactional
    @Override
    public void initiatePasswordRecovery(PasswordRecoveryDto recoveryDto) {
        User user = usersRepository.findByEmail(recoveryDto.getEmail())
                .orElseThrow(() -> new NotFoundException("User with email not found: ", recoveryDto.getEmail()));

        UserConfirmationCode userConfirmationCode = UserConfirmationCode.builder()
                .user(user)
                .uuid(UUID.randomUUID().toString())
                .expiredTime(LocalDateTime.now().plusDays(confirmLinkExpiredDays))
                .build();

        accountsConfirmRepository.save(userConfirmationCode);

        emailService.sendPasswordResetEmail(userConfirmationCode);
    }

    @Transactional
    @Override
    public StandardResponseDto changePassword(PasswordChangeDto changeDto, Long userId) {
        User user = getUserOrThrow(userId);

        if (!passwordEncoder.matches(changeDto.getOldPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "Invalid old password.");
        }

        String newPasswordHash = passwordEncoder.encode(changeDto.getNewPassword());
        user.setPasswordHash(newPasswordHash);
        usersRepository.save(user);

        emailService.sendChangePasswordNotification(user.getEmail());

        return StandardResponseDto.builder()
                .message("Your password has been successfully changed")
                .build();
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> usersList = usersRepository.findAll();

        return UserDto.from(usersList);
    }

    @Override
    public User getUserByEmailOrThrow(String email) {
        return findUserByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found", email));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserByZoomEmail(String email) {
        return usersRepository.findByZoomEmail(email);
    }

    @Transactional
    @Override
    public UserDto updateUser(UpdateUserDto newUserDto, Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));

        User user = getUserOrThrow(userId);

        if (isAdmin) {
            updateAllFields(newUserDto, user);
        } else if (authentication.getName().equals(user.getEmail())) {
            updateOwnFields(newUserDto, user);
        } else
            throw new BadRequestException("Access denied. You cant change data another user. Please contact with your Admin");
        usersRepository.save(user);
        return from(user);
    }

    /**
     * Updates the user's own fields with new values from the provided DTO.
     *
     * @param newUserDto The DTO containing the new user details.
     * @param user       The user entity to update.
     * @throws BadRequestException If an attempt is made to change the role or cohort.
     */
    private void updateOwnFields(NewUserDto newUserDto, User user) {
        updateFields(newUserDto, user);

        if (newUserDto.getRole() != null && !newUserDto.getRole().equals(user.getRole().name())) {
            throw new BadRequestException("Access denied. You cant change your Role");
        }

        if (newUserDto.getCohort() != null && !newUserDto.getCohort().equals(user.getPrimaryCohort().getAlias())) {
            throw new BadRequestException("You can't change cohort");
        }

    }

    /**
     * Updates all fields of the user with new values from the provided DTO.
     *
     * @param updatedUserDto The DTO containing the updated user details.
     * @param user           The user entity to update.
     * @throws BadRequestException If the new Zoom account already exists.
     */
    private void updateAllFields(UpdateUserDto updatedUserDto, User user) {
        updateFields(updatedUserDto, user);

        if (updatedUserDto.getRole() != null) {
            user.setRole(Role.valueOf(updatedUserDto.getRole()));
        }

        if (updatedUserDto.getCohort() != null) {
            Cohort cohort = cohortService.getCohortByAliasOrThrow(updatedUserDto.getCohort());
            user.setPrimaryCohort(cohort);
        }
        if (user.getRole().name().equalsIgnoreCase("TEACHER") && !usersRepository.existsByZoomAccount(updatedUserDto.getZoomAccount())) {

            updateFieldIfNotNull(updatedUserDto.getZoomAccount(), user::setZoomAccount);
        } else if (updatedUserDto.getZoomAccount() != null
                && (user.getRole().name().equalsIgnoreCase("TEACHER"))
                && usersRepository.existsByZoomAccount(updatedUserDto.getZoomAccount())) {
            throw new BadRequestException("This ZoomID already exist");
        }
        usersRepository.save(user);
    }

    /**
     * Updates common fields of the user with new values from the provided DTO.
     *
     * @param newUserDto The DTO containing the new user details.
     * @param user       The user entity to update.
     */
    private void updateFields(NewUserDto newUserDto, User user) {
        updateFieldIfNotNull(newUserDto.getFirstName(), user::setFirstName);
        updateFieldIfNotNull(newUserDto.getLastName(), user::setLastName);
        updateFieldIfNotNull(newUserDto.getPhone(), user::setPhone);
        updateFieldIfNotNull(newUserDto.getCountry(), user::setCountry);
    }

    /**
     * Updates a field if the new value is not null and different from the current value.
     *
     * @param newValue   The new value for the field.
     * @param setterFunc The setter function to update the field.
     */
    private void updateFieldIfNotNull(String newValue, Consumer<String> setterFunc) {
        if (newValue != null && !newValue.equals(setterFunc.toString())) {
            setterFunc.accept(newValue);
        }
    }

    @Transactional
    @Override
    public UserDto updatePrimaryCohort(Long userId, UpdateCohortDto updateCohortDto) {
        User user = getUserOrThrow(userId);
        Cohort primaryCohort = cohortService.getCohortByIdOrThrow(updateCohortDto.getCohortId());

        user.setPrimaryCohort(primaryCohort);
        usersRepository.save(user);
        return from(user);
    }

    @Transactional
    @Override
    public UserDto updateAdditionalCohort(Long userId, UpdateCohortsDto updateCohortsDto) {
        User user = getUserOrThrow(userId);
        user.getCohorts().clear();
        Set<Cohort> cohortSet = cohortService.findByIdIn(updateCohortsDto.getCohortIds());
        user.setCohorts(cohortSet);
        usersRepository.save(user);
        return from(user);
    }

    @Override
    public boolean isUserWithEmailExists(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    @Transactional
    @Override
    public List<StudentDto> addAdditionalCohortsByUsers(UpdateUsersAdditionalCohorts updateCohortsDto) {
        List<StudentDto> result = new ArrayList<>();
        for (Long userId : updateCohortsDto.getUserIds()) {
            User user = getUserOrThrow(userId);
            Set<Cohort> cohortSet = cohortService.findByIdIn(updateCohortsDto.getCohortIds());
            user.getCohorts().addAll(cohortSet);
            User modifiedUser = usersRepository.save(user);
            result.add(StudentDto.from(modifiedUser));
        }
        return result;
    }

    @Transactional
    @Override
    public StudentDto removeAdditionalCohortsByUser(Long userId, Long cohortId) {
        User user = getUserOrThrow(userId);
        user.getCohorts().removeIf(cohort -> cohort.getId().equals(cohortId));
        User modifiedUser = usersRepository.save(user);
        return StudentDto.from(modifiedUser);
    }

    @Transactional
    @Override
    public List<StudentDto> updatePrimaryCohortByUsers(Long cohortId, List<Long> userIds) {
        List<StudentDto> result = new ArrayList<>();
        for (Long userId : userIds) {
            User user = getUserOrThrow(userId);
            Cohort selectedCohort = cohortService.getCohortByIdOrThrow(cohortId);
            user.setPrimaryCohort(selectedCohort);
            User modifiedUser = usersRepository.save(user);
            result.add(StudentDto.from(modifiedUser));
        }
        return result;
    }

    @Override
    public List<StudentDto> getAllStudents() {
        return usersRepository.findAllByRole(Role.STUDENT)
                .stream()
                .map(StudentDto::from)
                .collect(Collectors.toList());
    }

}
