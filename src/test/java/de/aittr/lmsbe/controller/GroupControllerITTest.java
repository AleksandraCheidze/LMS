package de.aittr.lmsbe.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("GroupController is works: ")
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GroupControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAllCohorts_UserRole_ShouldAllowAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cohorts"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllCohorts_AdminRole_ShouldAllowAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cohorts"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "TEACHER")
    void getAllCohorts_TeacherRole_ShouldAllowAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cohorts"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void getAllCohorts_NoAuthentication_ShouldAllowAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cohorts"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
