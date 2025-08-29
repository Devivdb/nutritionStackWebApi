package com.nutritionstack.nutritionstackwebapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutritionstack.nutritionstackwebapi.dto.AdminUserRoleUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.model.User;
import com.nutritionstack.nutritionstackwebapi.model.UserRole;
import com.nutritionstack.nutritionstackwebapi.repository.UserRepository;
import com.nutritionstack.nutritionstackwebapi.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class AdminControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User adminUser;
    private User regularUser;
    private String adminToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Clean up database
        userRepository.deleteAll();

        // Create test users
        adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.ADMIN)
                .build();
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser = userRepository.save(adminUser);

        regularUser = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        regularUser.setCreatedAt(LocalDateTime.now());
        regularUser = userRepository.save(regularUser);

        // Generate admin token
        adminToken = jwtService.generateToken(adminUser);
    }

    @Test
    void should_GetAllUsers_When_AdminAuthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[0].role").exists())
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    void should_GetUserInfo_When_AdminAuthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(regularUser.getId()))
                .andExpect(jsonPath("$.username").value(regularUser.getUsername()))
                .andExpect(jsonPath("$.role").value(regularUser.getRole().name()));
    }

    @Test
    void should_UpdateUserRole_When_AdminAuthenticated() throws Exception {
        AdminUserRoleUpdateRequestDTO request = new AdminUserRoleUpdateRequestDTO();
        request.setRole(UserRole.ADMIN);

        mockMvc.perform(patch("/api/admin/users/" + regularUser.getId() + "/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.role").value(UserRole.ADMIN.name()));
    }

    @Test
    void should_DeleteUser_When_AdminAuthenticated() throws Exception {
        // Create another user to delete
        User userToDelete = User.builder()
                .username("usertodelete")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        userToDelete.setCreatedAt(LocalDateTime.now());
        userToDelete = userRepository.save(userToDelete);

        mockMvc.perform(delete("/api/admin/users/" + userToDelete.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify user was deleted
        assertFalse(userRepository.findById(userToDelete.getId()).isPresent());
    }

    @Test
    void should_ReturnForbidden_When_UserNotAdmin() throws Exception {
        // Generate token for regular user
        String userToken = jwtService.generateToken(regularUser);

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_ReturnUnauthorized_When_NoToken() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_ReturnBadRequest_When_UpdateRoleWithInvalidData() throws Exception {
        mockMvc.perform(patch("/api/admin/users/" + regularUser.getId() + "/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\": null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_ReturnNotFound_When_UserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/admin/users/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
