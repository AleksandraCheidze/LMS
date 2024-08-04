package de.aittr.lmsbe.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.aittr.lmsbe.dto.UuidDto;
import de.aittr.lmsbe.repository.CohortsRepository;
import de.aittr.lmsbe.repository.UsersRepository;
import de.aittr.lmsbe.service.UsersService;
import de.aittr.lmsbe.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class UserRegistrationConfirmTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService userService;

    @Autowired
    private CohortsRepository cohortsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @Test
    @DisplayName("Check user confirmation with valid UUID")
    void testCheckUserConfirmationWithValidUUID() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("john.doe@example.com")
                .state("CONFIRMED")
                .build();

        UuidDto uuidDto = UuidDto.builder()
                .uuid("valid-uuid")
                .build();

        when(userService.confirmRegistration(uuidDto)).thenReturn(userDto);

        String request = objectMapper.writeValueAsString("valid-uuid");

        mockMvc.perform(post("/users/confirm/check")
                        .header("Content-Type", "application/json")
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.state").value(userDto.getState()));
    }

//    @Nested
//    @Test
//    @DisplayName("Set user password with valid UUID and strong password")
//    void testSetUserPasswordWithValidUUIDAndStrongPassword() throws Exception {
//        Cohort cohort = Cohort.builder()
//                .id(1L)
//                .name("cohort27")
//                .alias("cohort27")
//                .build();
//        cohortsRepository.save(cohort);
//
//        User user = User.builder()
//                .email("john.doe@example.com")
//                .firstName("John")
//                .lastName("Doe")
//                .build();
//        usersRepository.save(user);
//
//        PasswordDto passwordDto = new PasswordDto();
//        passwordDto.setUuid("valid-uuid");
//        passwordDto.setPassword("StrongP@ssword123");
//
//        String request = objectMapper.writeValueAsString(passwordDto);
//
//        doNothing().when(userService).setPassword(eq(123L), any(PasswordDto.class));
//
//        mockMvc.perform(post("/api/users/{userId}/password","1")
//                        .header("Content-Type", "application/json")
//                        .content(request))
//                .andExpect(status().isOk());
//    }
//
//
//    @Nested
//    @Test
//    @DisplayName("Set user password with invalid UUID")
//    void testSetUserPasswordWithInvalidUUID() throws Exception {
//        PasswordDto passwordDto = new PasswordDto();
//        passwordDto.setUuid("invalid-uuid");
//        passwordDto.setPassword("StrongP@ssword123");
//
//        doThrow(new IllegalArgumentException("Invalid or expired UUID or User ID"))
//                .when(userService).setPassword(anyLong(), any());
//
//        mockMvc.perform(post("/api/users/{userId}/password","1")
//                        .header("Content-Type", "application/json")
//                        .content(String.valueOf(passwordDto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Invalid or expired UUID or User ID"));
//    }

}