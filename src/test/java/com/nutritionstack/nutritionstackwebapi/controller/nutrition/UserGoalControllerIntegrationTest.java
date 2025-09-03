package com.nutritionstack.nutritionstackwebapi.controller.nutrition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.auth.UserRole;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import com.nutritionstack.nutritionstackwebapi.repository.nutrition.UserGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import jakarta.persistence.EntityManager;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserGoalControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserGoalRepository userGoalRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        objectMapper = new ObjectMapper();
        
        testUser = new User("testuser", "password123", UserRole.USER);
        testUser = userRepository.save(testUser);
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void createGoal_ValidRequest_ShouldReturnCreatedGoal() throws Exception {
        UserGoalCreateRequestDTO request = new UserGoalCreateRequestDTO(2000.0, 150.0, 200.0, 67.0);
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/user-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caloriesGoal").value(2000.0))
                .andExpect(jsonPath("$.proteinGoal").value(150.0))
                .andExpect(jsonPath("$.carbsGoal").value(200.0))
                .andExpect(jsonPath("$.fatGoal").value(67.0))
                .andExpect(jsonPath("$.isActive").value(true));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void createGoal_InvalidCalories_ShouldReturnUnprocessableEntity() throws Exception {
        UserGoalCreateRequestDTO request = new UserGoalCreateRequestDTO(-100.0, 150.0, 200.0, 67.0);
        String requestJson = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/user-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnprocessableEntity());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void getActiveGoal_ExistingGoal_ShouldReturnGoal() throws Exception {
        UserGoal goal = new UserGoal(testUser, 2000.0, 150.0, 200.0, 67.0);
        userGoalRepository.save(goal);
        
        mockMvc.perform(get("/api/user-goals/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caloriesGoal").value(2000.0))
                .andExpect(jsonPath("$.proteinGoal").value(150.0));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void getActiveGoal_NoActiveGoal_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/user-goals/active"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void getAllUserGoals_ExistingGoals_ShouldReturnList() throws Exception {
        UserGoal goal1 = new UserGoal(testUser, 2000.0, 150.0, 200.0, 67.0);
        UserGoal goal2 = new UserGoal(testUser, 1800.0, 120.0, 180.0, 60.0);
        goal2.setIsActive(false);
        userGoalRepository.save(goal1);
        userGoalRepository.save(goal2);
        
        mockMvc.perform(get("/api/user-goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].caloriesGoal").value(1800.0))
                .andExpect(jsonPath("$[1].caloriesGoal").value(2000.0));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void updateGoal_ValidUpdate_ShouldReturnUpdatedGoal() throws Exception {
        UserGoal goal = new UserGoal(testUser, 2000.0, 150.0, 200.0, 67.0);
        goal = userGoalRepository.save(goal);
        
        UserGoalUpdateRequestDTO updateRequest = new UserGoalUpdateRequestDTO(2200.0, null, null, null);
        String requestJson = objectMapper.writeValueAsString(updateRequest);
        
        mockMvc.perform(put("/api/user-goals/" + goal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caloriesGoal").value(2200.0));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void deleteGoal_ExistingGoal_ShouldReturnNoContent() throws Exception {
        UserGoal goal = new UserGoal(testUser, 2000.0, 150.0, 200.0, 67.0);
        goal = userGoalRepository.save(goal);
        
        mockMvc.perform(delete("/api/user-goals/" + goal.getId()))
                .andExpect(status().isNoContent());
        
        UserGoal deletedGoal = userGoalRepository.findById(goal.getId()).orElse(null);
        assert deletedGoal != null;
        assert !deletedGoal.getIsActive();
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void reactivateGoal_ExistingInactiveGoal_ShouldReactivateGoal() throws Exception {
        UserGoal activeGoal = new UserGoal(testUser, 2000.0, 150.0, 200.0, 67.0);
        activeGoal.setIsActive(true);
        activeGoal = userGoalRepository.save(activeGoal);
        
        UserGoal inactiveGoal = new UserGoal(testUser, 1800.0, 120.0, 180.0, 60.0);
        inactiveGoal.setIsActive(false);
        inactiveGoal = userGoalRepository.save(inactiveGoal);
        
        mockMvc.perform(post("/api/user-goals/" + inactiveGoal.getId() + "/reactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inactiveGoal.getId()))
                .andExpect(jsonPath("$.caloriesGoal").value(1800.0))
                .andExpect(jsonPath("$.isActive").value(true));
        
        entityManager.flush();
        entityManager.clear();
        
        UserGoal reactivatedGoal = userGoalRepository.findById(inactiveGoal.getId()).orElse(null);
        UserGoal previouslyActiveGoal = userGoalRepository.findById(activeGoal.getId()).orElse(null);
        
        assert reactivatedGoal != null;
        assert previouslyActiveGoal != null;
        assert reactivatedGoal.getIsActive() : "Reactivated goal should be active";
        assert !previouslyActiveGoal.getIsActive() : "Previously active goal should be inactive";
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void reactivateGoal_NonExistentGoal_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/user-goals/999/reactivate"))
                .andExpect(status().isBadRequest());
    }
}
