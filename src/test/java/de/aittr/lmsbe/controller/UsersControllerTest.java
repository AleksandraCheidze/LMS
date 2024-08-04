package de.aittr.lmsbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.aittr.lmsbe.dto.*;
import de.aittr.lmsbe.exception.BadRequestException;
import de.aittr.lmsbe.exception.NotFoundException;
import de.aittr.lmsbe.model.Cohort;
import de.aittr.lmsbe.model.User;
import de.aittr.lmsbe.model.UserConfirmationCode;
import de.aittr.lmsbe.repository.AccountsConfirmRepository;
import de.aittr.lmsbe.repository.CohortsRepository;
import de.aittr.lmsbe.repository.UsersRepository;
import de.aittr.lmsbe.service.UsersService;
import de.aittr.lmsbe.test.config.TestConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
@DisplayName("UsersController is works: ")
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("test")
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CohortsRepository cohortsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsersService usersService;

    @Autowired
    private AccountsConfirmRepository accountsConfirmRepository;

    @Nested
    @DisplayName("POST /users is working: ")
    class AddUserTests {
        @Test
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void addUser() throws Exception {
            Cohort cohort = Cohort.builder()
                    .id(1L)
                    .githubRepository("cohort27")
                    .name("cohort27")
                    .alias("Cohort 27")
                    .build();
            cohortsRepository.save(cohort);

            NewUserDto user = NewUserDto.builder()
                    .cohort("Cohort 27")
                    .email("john.doe@example.com")
                    .firstName("John")
                    .lastName("Doe")
                    .build();
            String request = objectMapper.writeValueAsString(user);

            mockMvc.perform(post("/users")
                            .header("Content-Type", "application/json")
                            .content(request))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                    .andExpect(jsonPath("$.role", is("STUDENT")))
                    .andExpect(jsonPath("$.state", is("NOT_CONFIRMED")))
                    .andExpect(jsonPath("$.firstName", is("John")))
                    .andExpect(jsonPath("$.lastName", is("Doe")))
                    .andExpect(jsonPath("$.country", is(nullValue())))
                    .andExpect(jsonPath("$.phone", is(nullValue())));
        }
    }

    @Nested
    @DisplayName("GET /users/my/cohorts is working: ")
    class getAllCohortsByUserTests {

        @Test
        @DisplayName("GET /users/my/cohorts for STUDENT is working: ")
        @Sql(scripts = "/sql/data_for_users.sql")
        @WithUserDetails(value = "john.doe@example.com")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void getAllCohortsByUser() throws Exception {
            mockMvc.perform(get("/users/my/cohorts"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /users is working: ")
    class getAllUsersTests {

        @BeforeEach
        void setupUsers() {

            Cohort cohort = Cohort.builder()
                    .id(1L)
                    .githubRepository("cohort27")
                    .name("cohort27")
                    .alias("Cohort 27")
                    .build();
            cohortsRepository.save(cohort);

            User adminUser = User.builder()
                    .email("admin@gmail.com")
                    .role(User.Role.ADMIN)
                    .state(User.State.CONFIRMED)
                    .passwordHash("$2a$10$lcFUtI4rKpUyrRb0R416Bu6UTl1KvHhwzkMWpsqdvcQiYjrqCGP72")
                    .firstName("John")
                    .lastName("Doe")
                    .country("testLand")
                    .phone("+4")
                    .isActive(true)
                    .primaryCohort(cohort)
                    .cohorts(new HashSet<>())
                    .build();
            usersRepository.save(adminUser);

            User studentUser = User.builder()
                    .email("student@gmail.com")
                    .role(User.Role.STUDENT)
                    .state(User.State.CONFIRMED)
                    .passwordHash("$2a$10$lcFUtI4rKpUyrRb0R416Bu6UTl1KvHhwzkMWpsqdvcQiYjrqCGP72")
                    .firstName("John")
                    .lastName("Doe")
                    .country("testLand")
                    .phone("+4")
                    .isActive(true)
                    .primaryCohort(cohort)
                    .cohorts(new HashSet<>())
                    .build();
            usersRepository.save(studentUser);

            User teacherUser = User.builder()
                    .email("teacher@gmail.com")
                    .role(User.Role.TEACHER)
                    .state(User.State.CONFIRMED)
                    .passwordHash("$2a$10$lcFUtI4rKpUyrRb0R416Bu6UTl1KvHhwzkMWpsqdvcQiYjrqCGP72")
                    .firstName("John")
                    .lastName("Doe")
                    .country("testLand")
                    .phone("+4")
                    .isActive(true)
                    .primaryCohort(cohort)
                    .cohorts(new HashSet<>())
                    .build();
            usersRepository.save(teacherUser);
        }

        @Test
        @DisplayName("GET /users for ADMIN is working: ")
        @WithMockUser(username = "admin@gmail.com", authorities = {"ADMIN"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        public void testGetAllUsersAsAdmin() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
        }

        @Test
        @DisplayName("GET /users for STUDENT is working: ")
        @WithMockUser(username = "student@gmail.com", roles = "STUDENT")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        public void testGetAllUsersAsStudent() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        @DisplayName("GET /users for TEACHER is working: ")
        @WithMockUser(username = "teacher@gmail.com", roles = "TEACHER")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        public void testGetAllUsersAsTeacher() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }


    @Nested
    @DisplayName("POST /{userId}/password is working: ")
    class SetPasswordTests {

        @Test
        @Sql(scripts = "/sql/data_for_setPass.sql")
        @WithUserDetails(value = "john.doe@example.com")
        @DisplayName("Set Password With Valid Data working: ")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testSetPasswordWithValidData() throws Exception {
            String newPassword = "Strong_password-123";
            String uuid = "6c2f764a-9f9c-4856-8bad-61097e099e7c";
            PasswordDto passwordDto = new PasswordDto(uuid, newPassword);
            String request = objectMapper.writeValueAsString(passwordDto);
            mockMvc.perform(post("/users/" + 1 + "/password")
                            .header("Content-Type", "application/json")
                            .content(request))
                    .andExpect(status().isOk());

        }

        @Test
        @Sql(scripts = "/sql/data_for_setPass.sql")
        @DisplayName("Change Password With Mismatched User Id: ")
        @WithUserDetails(value = "john.doe@example.com")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testChangePasswordWithMismatchedUserId() throws Exception {
            String newPassword = "Strong_password-123";
            String uuid = "6c2f764a-9f9c-4856-8bad-61097e099e7c";
            PasswordDto passwordDto = new PasswordDto(uuid, newPassword);
            String request = objectMapper.writeValueAsString(passwordDto);
            mockMvc.perform(post("/users/" + 2 + "/password")
                            .header("Content-Type", "application/json")
                            .content(request))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @Sql(scripts = "/sql/data_for_setPass.sql")
        @WithUserDetails(value = "john.doe@example.com")
        @DisplayName("Set Password And Check New State: ")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testSetPasswordAndCheckNewState() {

            String newPassword = "Strong_password-123";
            String uuid = "6c2f764a-9f9c-4856-8bad-61097e099e7c";
            PasswordDto passwordDto = new PasswordDto(uuid, newPassword);
            usersService.setPassword(1L, passwordDto);
            assertEquals(usersService.getUser(1L).getState(), "CONFIRMED");

        }

        @Test
        @Sql(scripts = "/sql/data_for_setPass.sql")
        @WithUserDetails(value = "john.doe@example.com")
        @DisplayName("Set Password with Wrong UUID: ")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testSetPasswordWrongUUID() {

            String newPassword = "Strong_password-123";
            String uuid = UUID.randomUUID().toString();
            PasswordDto passwordDto = new PasswordDto(uuid, newPassword);
            assertThrows(BadRequestException.class, () -> usersService.setPassword(1L, passwordDto));
        }

        @Test
        @Sql(scripts = "/sql/data_for_setPass.sql")
        @WithUserDetails(value = "john.doe@example.com")
        @DisplayName("Set Password with weak password: ")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testSetWeakPassword() throws Exception {
            String newPassword = "12";
            String uuid = "6c2f764a-9f9c-4856-8bad-61097e099e7c";
            PasswordDto passwordDto = new PasswordDto(uuid, newPassword);
            String request = objectMapper.writeValueAsString(passwordDto);
            mockMvc.perform(post("/users/" + 1 + "/password")
                            .header("Content-Type", "application/json")
                            .content(request))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /confirm/check is working: ")
    class confirmRegistrationTests {

        @Test
        @Sql(scripts = "/sql/data_for_confirm_registration.sql")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void confirmRegistrationPositiveTest() throws Exception {

            UuidDto uuidDto = new UuidDto();
            uuidDto.setUuid("998bf356-8ad6-4985-817e-bca1821fbe0b");

            mockMvc.perform(post("/users/confirm/check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(uuidDto)))
                    .andExpect(status().isOk());
        }

        @Test
        @Sql(scripts = "/sql/data_for_confirm_registration.sql")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void confirmRegistrationNegativeValidationErrorTest() throws Exception {

            UuidDto uuidDto = new UuidDto();
            uuidDto.setUuid("998bf356-8ad6-4985-817e-nnnnnnn");

            mockMvc.perform(post("/users/confirm/check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(uuidDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("Invalid UUID")));
        }

        private String asJsonString(final Object obj) {
            try {
                return new ObjectMapper().writeValueAsString(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("POST /users/password-recover is working: ")
    class initiatePasswordRecoveryTests {

        @Test
        @Sql(scripts = "/sql/data_for_users.sql")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void initiatePasswordRecoveryPositiveTest() throws Exception {

            PasswordRecoveryDto recoveryDto = createRecoveryDto("john.doe@example.com");

            mockMvc.perform(post("/users/password-recovery")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(recoveryDto)))
                    .andExpect(status().isCreated());

            UserConfirmationCode savedCode = accountsConfirmRepository.findByUserId(1L);
            assertNotNull(savedCode);
            assertEquals(recoveryDto.getEmail(), savedCode.getUser().getEmail());
        }

        @Test
        @Sql(scripts = "/sql/data_for_users.sql")
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void initiatePasswordRecoveryNegativeTestIncorrectEmail() throws Exception {

            PasswordRecoveryDto recoveryDto = createRecoveryDto("not.correct.email@example.com");

            mockMvc.perform(post("/users/password-recovery")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(recoveryDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("User with email not found:  with name <not.correct.email@example.com> not found."));
        }

        private String asJsonString(final Object obj) {
            try {
                return new ObjectMapper().writeValueAsString(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private PasswordRecoveryDto createRecoveryDto(String email) {
            PasswordRecoveryDto recoveryDto = new PasswordRecoveryDto();
            recoveryDto.setEmail(email);
            return recoveryDto;
        }
    }

    @Nested
    @DisplayName("POST update user is working:")
    class updateUserTests {
        @BeforeEach
        void setupUsers() {
            Cohort cohort = Cohort.builder()
                    .id(1L)
                    .githubRepository("cohort33")
                    .name("cohort33")
                    .alias("Cohort 33")
                    .build();
            cohortsRepository.save(cohort);

            Cohort cohort2 = Cohort.builder()
                    .id(2L)
                    .githubRepository("cohort34")
                    .name("cohort34")
                    .alias("Cohort 34")
                    .build();
            cohortsRepository.save(cohort2);

            Cohort cohort3 = Cohort.builder()
                    .id(3L)
                    .githubRepository("cohort35")
                    .name("cohort35")
                    .alias("Cohort 36")
                    .build();
            cohortsRepository.save(cohort3);

            User adminUser = User.builder()
                    .email("admin@gmail.com")
                    .role(User.Role.ADMIN)
                    .state(User.State.CONFIRMED)
                    .passwordHash("$2a$10$lcFUtI4rKpUyrRb0R416Bu6UTl1KvHhwzkMWpsqdvcQiYjrqCGP72")
                    .firstName("John")
                    .lastName("Doe")
                    .country("Holland")
                    .phone("+4915174745213")
                    .isActive(true)
                    .primaryCohort(cohort)
                    .cohorts(new HashSet<>())
                    .build();
            usersRepository.save(adminUser);

            User studentUser = User.builder()
                    .email("student@gmail.com")
                    .role(User.Role.STUDENT)
                    .state(User.State.CONFIRMED)
                    .passwordHash("$2a$10$lcFUtI4rKpUyrRb0R416Bu6UTl1KvHhwzkMWpsqdvcQiYjrqCGP72")
                    .firstName("Student")
                    .lastName("Studentov")
                    .country("England")
                    .phone("+4784554745256")
                    .isActive(true)
                    .primaryCohort(cohort)
                    .cohorts(new HashSet<>())
                    .build();
            usersRepository.save(studentUser);

            User secindStudentUser = User.builder()
                    .email("secondStudent@gmail.com")
                    .role(User.Role.STUDENT)
                    .state(User.State.CONFIRMED)
                    .passwordHash("$2a$10$lcFUtI4rKpUyrRb0R416Bu6UTl1KvHhwzkMWpsqdvcQiYjrqCGP72")
                    .firstName("Student2")
                    .lastName("Studentov2")
                    .country("Ireland")
                    .phone("+4784554745256")
                    .isActive(true)
                    .primaryCohort(cohort)
                    .cohorts(new HashSet<>())
                    .build();
            usersRepository.save(secindStudentUser);

            User teacherUser = User.builder()
                    .email("teacher@gmail.com")
                    .role(User.Role.TEACHER)
                    .state(User.State.CONFIRMED)
                    .passwordHash("$2a$10$lcFUtI4rKpUyrRb0R416Bu6UTl1KvHhwzkMWpsqdvcQiYjrqCGP72")
                    .firstName("John")
                    .lastName("Wick")
                    .country("USA")
                    .phone("+4900000000000")
                    .isActive(true)
                    .primaryCohort(cohort)
                    .cohorts(new HashSet<>())
                    .build();
            usersRepository.save(teacherUser);

            User secondTeacherUser = User.builder()
                    .email("teacher2@gmail.com")
                    .role(User.Role.TEACHER)
                    .state(User.State.CONFIRMED)
                    .passwordHash("$2a$10$lcFUtI4rKpUyrRb0R416Bu6UTl1KvHhwzkMWpsqdvcQiYjrqCGP72")
                    .firstName("John2")
                    .lastName("Wick2")
                    .country("USA2")
                    .phone("+4900000000002")
                    .isActive(true)
                    .primaryCohort(cohort)
                    .cohorts(new HashSet<>())
                    .build();
            usersRepository.save(secondTeacherUser);
        }

        @Test
        @WithMockUser(username = "admin@gmail.com", authorities = {"ADMIN"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testAdminChangeAdmin() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Joshua")
                    .lastName("McCirby")
                    .phone("+4912140000000")
                    .country("Legoland")
                    .role("STUDENT")
                    .cohort("Cohort 33")
                    .build();
            UserDto result = usersService.updateUser(newUserDto, 1L);
            assertEquals("Joshua", result.getFirstName());
            assertEquals("McCirby", result.getLastName());
            assertEquals("+4912140000000", result.getPhone());
            assertEquals("Legoland", result.getCountry());
            assertEquals("STUDENT", result.getRole());
            assertEquals("Cohort 33", result.getPrimaryCohort());

        }

        @Test
        @WithMockUser(username = "admin@gmail.com", authorities = {"ADMIN"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testAdminChangeStudent() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Joshua")
                    .lastName("McCirby")
                    .phone("+4912140000000")
                    .country("Legoland")
                    .role("TEACHER")
                    .cohort("Cohort 33")
                    .build();
            UserDto result = usersService.updateUser(newUserDto, 2L);
            assertEquals("Joshua", result.getFirstName());
            assertEquals("McCirby", result.getLastName());
            assertEquals("+4912140000000", result.getPhone());
            assertEquals("Legoland", result.getCountry());
            assertEquals("TEACHER", result.getRole());
            assertEquals("Cohort 33", result.getPrimaryCohort());
        }

        @Test
        @WithMockUser(username = "admin@gmail.com", authorities = {"ADMIN"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testAdminChangeTeacher() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Joshua")
                    .lastName("McCirby")
                    .phone("+4912140000000")
                    .country("Legoland")
                    .role("STUDENT")
                    .build();
            UserDto result = usersService.updateUser(newUserDto, 2L);
            assertEquals("Joshua", result.getFirstName());
            assertEquals("McCirby", result.getLastName());
            assertEquals("+4912140000000", result.getPhone());
            assertEquals("Legoland", result.getCountry());
            assertEquals("STUDENT", result.getRole());

        }

        @Test
        @WithMockUser(username = "admin@gmail.com", authorities = {"ADMIN"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testAdminChangeZoomAccountToTeacher() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .zoomAccount("TestZoomID")
                    .build();
            UserDto result = usersService.updateUser(newUserDto, 4L);
            assertEquals("TestZoomID", result.getZoomAccount());
        }

        @Test
        @WithMockUser(username = "admin@gmail.com", authorities = {"ADMIN"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testAdminChangeZoomAccountToStudent() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .zoomAccount("StudentZoomID")
                    .build();
            UserDto result = usersService.updateUser(newUserDto, 2L);
            assertNull(result.getZoomAccount());
        }

        @Test
        @WithMockUser(username = "student@gmail.com", authorities = {"STUDENT"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testStudentChangeHimself() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Joshua")
                    .lastName("McCirby")
                    .phone("+4912140000000")
                    .country("Legoland")
                    .build();
            UserDto result = usersService.updateUser(newUserDto, 2L);
            assertEquals("Joshua", result.getFirstName());
            assertEquals("McCirby", result.getLastName());
            assertEquals("+4912140000000", result.getPhone());
            assertEquals("Legoland", result.getCountry());
        }

        @Test
        @WithMockUser(username = "student@gmail.com", authorities = {"STUDENT"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testStudentChangeAdmin() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Joshua")
                    .lastName("McCirby")
                    .phone("+4912140000000")
                    .country("Legoland")
                    .role("TEACHER")
                    .build();

            assertThrows(BadRequestException.class, () -> usersService.updateUser(newUserDto, 1L));
        }

        @Test
        @WithMockUser(username = "student@gmail.com", authorities = {"STUDENT"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testStudentChangeAnotherStudent() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Joshua")
                    .lastName("McCirby")
                    .phone("+4912140000000")
                    .country("Legoland")
                    .role("TEACHER")
                    .build();

            assertThrows(BadRequestException.class, () -> usersService.updateUser(newUserDto, 3L));
        }

        @Test
        @WithMockUser(username = "student@gmail.com", authorities = {"STUDENT"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testStudentChangeTeacher() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Joshua")
                    .lastName("McCirby")
                    .phone("+4912140000000")
                    .country("Legoland")
                    .role("TEACHER")
                    .build();

            assertThrows(BadRequestException.class, () -> usersService.updateUser(newUserDto, 3L));
        }

        @Test
        @WithMockUser(username = "student@gmail.com", authorities = {"STUDENT"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testStudentChangeForbiddenField() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .role("TEACHER")
                    .build();
            assertThrows(BadRequestException.class, () -> usersService.updateUser(newUserDto, 2L));


        }

        @Test
        @WithMockUser(username = "student@gmail.com", authorities = {"STUDENT"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testStudentChangeForbiddenFieldCohort() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .cohort("Cohort 34")
                    .build();
            assertThrows(BadRequestException.class, () -> usersService.updateUser(newUserDto, 2L));

        }


        @Test
        @WithMockUser(username = "teacher@gmail.com", authorities = {"TEACHER"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testTeacherChangeHimself() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Joshua")
                    .lastName("McCirby")
                    .phone("+4912140000000")
                    .country("Legoland")
                    .build();
            UserDto result = usersService.updateUser(newUserDto, 4L);
            assertEquals("Joshua", result.getFirstName());
            assertEquals("McCirby", result.getLastName());
            assertEquals("+4912140000000", result.getPhone());
            assertEquals("Legoland", result.getCountry());
        }

        @Test
        @WithMockUser(username = "teacher@gmail.com", authorities = {"TEACHER"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testTeacherChangeHimselfForbiddenField() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .role("ADMIN")
                    .build();
            assertThrows(BadRequestException.class, () -> usersService.updateUser(newUserDto, 4L));
        }

        @Test
        @WithMockUser(username = "teacher@gmail.com", authorities = {"TEACHER"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testTeacherChangeStudent() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Pedro")
                    .build();
            assertThrows(BadRequestException.class, () -> usersService.updateUser(newUserDto, 2L));
        }

        @Test
        @WithMockUser(username = "teacher@gmail.com", authorities = {"TEACHER"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testTeacherChangeAdmin() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Pedro")
                    .build();
            assertThrows(BadRequestException.class, () -> usersService.updateUser(newUserDto, 1L));
        }

        @Test
        @WithMockUser(username = "teacher@gmail.com", authorities = {"TEACHER"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testTeacherChangeAnotherTeacher() {
            UpdateUserDto newUserDto = UpdateUserDto.builder()
                    .firstName("Pedro")
                    .build();
            assertThrows(BadRequestException.class, () -> usersService.updateUser(newUserDto, 5L));
        }

        @Test
        @WithMockUser(username = "admin@gmail.com", authorities = {"ADMIN"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testAdminChangePrimaryCohortStudent() {
            UpdateCohortDto updateCohortDto = new UpdateCohortDto();
            updateCohortDto.setCohortId(2L);
            UserDto result = usersService.updatePrimaryCohort(2L, updateCohortDto);
            assertEquals("Cohort 34", result.getPrimaryCohort());
        }

        @Test
        @WithMockUser(username = "admin@gmail.com", authorities = {"ADMIN"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testAdminChangePrimaryCohortNonExistingStudent() {
            UpdateCohortDto updateCohortDto = new UpdateCohortDto();
            updateCohortDto.setCohortId(2L);
            assertThrows(NotFoundException.class, () -> usersService.updatePrimaryCohort(12L, updateCohortDto));

        }


    }

    @Nested
    @DisplayName("POST update additional Cohort is working:")
    class updateAdditionalCohortTests {
        @Test
        @Transactional
        @WithMockUser(username = "admin@gmail.com", authorities = {"ADMIN"})
        @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
        void testAdminChangeAdditionalCohortStudent() {
            Cohort cohort = Cohort.builder()
                    .id(1L)
                    .githubRepository("cohort33")
                    .name("cohort33")
                    .alias("Cohort 33")
                    .build();
            cohortsRepository.save(cohort);

            Cohort cohort2 = Cohort.builder()
                    .id(2L)
                    .githubRepository("cohort34")
                    .name("cohort34")
                    .alias("Cohort 34")
                    .build();
            cohortsRepository.save(cohort2);

            Cohort cohort3 = Cohort.builder()
                    .id(3L)
                    .githubRepository("cohort35")
                    .name("cohort35")
                    .alias("Cohort 36")
                    .build();
            cohortsRepository.save(cohort3);

            User adminUser = User.builder()
                    .email("admin@gmail.com")
                    .role(User.Role.ADMIN)
                    .state(User.State.CONFIRMED)
                    .passwordHash("$2a$10$lcFUtI4rKpUyrRb0R416Bu6UTl1KvHhwzkMWpsqdvcQiYjrqCGP72")
                    .firstName("John")
                    .lastName("Doe")
                    .country("testLand")
                    .phone("+4")
                    .isActive(true)
                    .primaryCohort(cohort)
                    .cohorts(new HashSet<>())
                    .build();
            usersRepository.save(adminUser);

            User studentUser = User.builder()
                    .email("student@gmail.com")
                    .role(User.Role.STUDENT)
                    .state(User.State.CONFIRMED)
                    .passwordHash("$2a$10$lcFUtI4rKpUyrRb0R416Bu6UTl1KvHhwzkMWpsqdvcQiYjrqCGP72")
                    .firstName("John")
                    .lastName("Doe")
                    .country("testLand")
                    .phone("+4")
                    .isActive(true)
                    .primaryCohort(cohort)
                    .cohorts(new HashSet<>())
                    .build();
            usersRepository.save(studentUser);

            UpdateCohortsDto updateCohortsDto = new UpdateCohortsDto();
            List<Long> cohortIds = Arrays.asList(cohort.getId(), cohort3.getId());
            updateCohortsDto.setCohortIds(cohortIds);
            usersService.updateAdditionalCohort(studentUser.getId(), updateCohortsDto);

            System.out.println(studentUser.getCohorts());
            Set<Long> actualCohortIds = studentUser.getCohorts().stream()
                    .map(Cohort::getId)
                    .collect(Collectors.toSet());

            assertEquals(new HashSet<>(cohortIds), actualCohortIds);

        }
    }
}
