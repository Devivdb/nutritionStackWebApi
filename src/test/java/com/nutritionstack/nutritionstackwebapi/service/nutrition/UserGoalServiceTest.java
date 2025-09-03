package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalResponseDTO;
import com.nutritionstack.nutritionstackwebapi.exception.UserGoalNotFoundException;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import com.nutritionstack.nutritionstackwebapi.repository.nutrition.UserGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGoalServiceTest {
    
    @Mock
    private UserGoalRepository userGoalRepository;
    
    @Mock
    private UserGoalMapper userGoalMapper;
    
    @Mock
    private UserGoalValidator userGoalValidator;
    
    @InjectMocks
    private UserGoalService userGoalService;
    
    private User testUser;
    private UserGoal testGoal;
    private UserGoalCreateRequestDTO createRequest;
    private UserGoalUpdateRequestDTO updateRequest;
    private UserGoalResponseDTO responseDTO;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        
        testGoal = new UserGoal();
        testGoal.setId(1L);
        testGoal.setUser(testUser);
        testGoal.setCaloriesGoal(2000.0);
        testGoal.setProteinGoal(150.0);
        testGoal.setCarbsGoal(200.0);
        testGoal.setFatGoal(67.0);
        testGoal.setIsActive(true);
        testGoal.setCreatedAt(LocalDateTime.now());
        testGoal.setUpdatedAt(LocalDateTime.now());
        
        createRequest = new UserGoalCreateRequestDTO(2000.0, 150.0, 200.0, 67.0);
        updateRequest = new UserGoalUpdateRequestDTO(2200.0, null, null, null);
        
        responseDTO = new UserGoalResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCaloriesGoal(2000.0);
        responseDTO.setProteinGoal(150.0);
    }
    
    @Test
    void createGoal_ValidRequest_ShouldCreateAndReturnGoal() {
        when(userGoalMapper.createEntity(createRequest, 1L)).thenReturn(testGoal);
        when(userGoalRepository.save(any(UserGoal.class))).thenReturn(testGoal);
        when(userGoalMapper.toResponseDTO(testGoal)).thenReturn(responseDTO);
        
        UserGoalResponseDTO result = userGoalService.createGoal(createRequest, 1L);
        
        assertNotNull(result);
        assertEquals(2000.0, result.getCaloriesGoal());
        assertEquals(150.0, result.getProteinGoal());
        verify(userGoalValidator).validateCreateRequest(createRequest);
        verify(userGoalRepository).deactivateAllActiveGoals(1L);
        verify(userGoalRepository).save(any(UserGoal.class));
    }
    
    @Test
    void getActiveGoal_ExistingGoal_ShouldReturnGoal() {
        when(userGoalRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testGoal));
        when(userGoalMapper.toResponseDTO(testGoal)).thenReturn(responseDTO);
        
        UserGoalResponseDTO result = userGoalService.getActiveGoal(1L);
        
        assertNotNull(result);
        assertEquals(2000.0, result.getCaloriesGoal());
    }
    
    @Test
    void getActiveGoal_NoActiveGoal_ShouldThrowException() {
        when(userGoalRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());
        
        assertThrows(UserGoalNotFoundException.class, () -> userGoalService.getActiveGoal(1L));
    }
    
    @Test
    void getAllUserGoals_ExistingGoals_ShouldReturnList() {
        List<UserGoal> goals = Arrays.asList(testGoal);
        when(userGoalRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(goals);
        when(userGoalMapper.toResponseDTO(testGoal)).thenReturn(responseDTO);
        
        List<UserGoalResponseDTO> result = userGoalService.getAllUserGoals(1L);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2000.0, result.get(0).getCaloriesGoal());
    }
    
    @Test
    void updateGoal_ValidUpdate_ShouldUpdateAndReturnGoal() {
        when(userGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testGoal));
        when(userGoalRepository.save(any(UserGoal.class))).thenReturn(testGoal);
        when(userGoalMapper.toResponseDTO(testGoal)).thenReturn(responseDTO);
        
        UserGoalResponseDTO result = userGoalService.updateGoal(1L, updateRequest, 1L);
        
        assertNotNull(result);
        verify(userGoalValidator).validateUpdateRequest(updateRequest);
        verify(userGoalMapper).updateEntity(testGoal, updateRequest);
        verify(userGoalRepository).save(any(UserGoal.class));
    }
    
    @Test
    void deleteGoal_ExistingGoal_ShouldDeactivateGoal() {
        when(userGoalRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testGoal));
        when(userGoalRepository.save(any(UserGoal.class))).thenReturn(testGoal);
        
        userGoalService.deleteGoal(1L, 1L);
        
        verify(userGoalRepository).save(any(UserGoal.class));
        assertFalse(testGoal.getIsActive());
    }
    
    @Test
    void hasActiveGoal_ExistingActiveGoal_ShouldReturnTrue() {
        when(userGoalRepository.existsByUserIdAndIsActiveTrue(1L)).thenReturn(true);
        
        boolean result = userGoalService.hasActiveGoal(1L);
        
        assertTrue(result);
    }
    
    @Test
    void reactivateGoal_ExistingGoal_ShouldReactivateGoal() {
        UserGoal inactiveGoal = new UserGoal();
        inactiveGoal.setId(2L);
        inactiveGoal.setUser(testUser);
        inactiveGoal.setCaloriesGoal(1800.0);
        inactiveGoal.setProteinGoal(120.0);
        inactiveGoal.setIsActive(false);
        
        UserGoalResponseDTO reactivatedResponseDTO = new UserGoalResponseDTO();
        reactivatedResponseDTO.setId(2L);
        reactivatedResponseDTO.setCaloriesGoal(1800.0);
        reactivatedResponseDTO.setProteinGoal(120.0);
        reactivatedResponseDTO.setIsActive(true);
        
        when(userGoalRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(inactiveGoal));
        when(userGoalRepository.save(any(UserGoal.class))).thenReturn(inactiveGoal);
        when(userGoalMapper.toResponseDTO(inactiveGoal)).thenReturn(reactivatedResponseDTO);
        
        UserGoalResponseDTO result = userGoalService.reactivateGoal(2L, 1L);
        
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(1800.0, result.getCaloriesGoal());
        assertTrue(result.getIsActive());
        verify(userGoalRepository).deactivateAllActiveGoals(1L);
        verify(userGoalRepository).save(inactiveGoal);
        assertTrue(inactiveGoal.getIsActive());
    }
    
    @Test
    void reactivateGoal_NonExistentGoal_ShouldThrowException() {
        when(userGoalRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());
        
        assertThrows(UserGoalNotFoundException.class, () -> userGoalService.reactivateGoal(999L, 1L));
    }
}
