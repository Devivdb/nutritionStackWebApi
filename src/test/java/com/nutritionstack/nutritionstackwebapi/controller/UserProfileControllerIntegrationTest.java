package com.nutritionstack.nutritionstackwebapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutritionstack.nutritionstackwebapi.dto.UserProfileUpdateRequestDTO;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class UserProfileControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private String userToken;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        objectMapper = new ObjectMapper();
        
        // Clean up database
        userRepository.deleteAll();
        
        // Create test user
        testUser = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.USER)
                .build();
        testUser.setCreatedAt(LocalDateTime.now());
        testUser = userRepository.save(testUser);
        
        // Generate token
        userToken = jwtService.generateToken(testUser);
    }
    
    @Test
    void getUserProfile_ShouldReturnUserProfile_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
    
    @Test
    void getUserProfile_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void updateUserProfile_ShouldUpdateProfile_WhenValidRequest() throws Exception {
        UserProfileUpdateRequestDTO updateRequest = new UserProfileUpdateRequestDTO("newusername", "newpassword123");
        
        mockMvc.perform(patch("/api/user/profile")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newusername"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
    
    @Test
    void deleteUserAccount_ShouldDeleteAccount_WhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/user/profile")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }
}
