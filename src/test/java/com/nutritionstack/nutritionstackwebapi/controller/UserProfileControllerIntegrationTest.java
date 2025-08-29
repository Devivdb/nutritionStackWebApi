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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class UserProfileControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        objectMapper = new ObjectMapper();
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void getUserProfile_ShouldReturnUserProfile_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
    
    @Test
    void getUserProfile_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void updateUserProfile_ShouldUpdateProfile_WhenValidRequest() throws Exception {
        UserProfileUpdateRequestDTO updateRequest = new UserProfileUpdateRequestDTO("newusername", "newpassword123");
        
        mockMvc.perform(patch("/api/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newusername"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void deleteUserAccount_ShouldDeleteAccount_WhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/user/profile"))
                .andExpect(status().isNoContent());
    }
}
