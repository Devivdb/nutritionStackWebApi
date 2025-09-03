package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalResponseDTO;
import com.nutritionstack.nutritionstackwebapi.exception.UserGoalNotFoundException;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import com.nutritionstack.nutritionstackwebapi.repository.nutrition.UserGoalRepository;
import com.nutritionstack.nutritionstackwebapi.util.UserGoalValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGoalService {
    
    private final UserGoalRepository userGoalRepository;
    private final UserGoalMapper userGoalMapper;
    private final UserGoalValidator userGoalValidator;
    
    public UserGoalService(UserGoalRepository userGoalRepository, 
                          UserGoalMapper userGoalMapper,
                          UserGoalValidator userGoalValidator) {
        this.userGoalRepository = userGoalRepository;
        this.userGoalMapper = userGoalMapper;
        this.userGoalValidator = userGoalValidator;
    }

    @Transactional
    public UserGoalResponseDTO createGoal(UserGoalCreateRequestDTO request, Long userId) {
        userGoalValidator.validateCreateRequest(request);
        
        userGoalRepository.deactivateAllActiveGoals(userId);
        
        UserGoal newGoal = userGoalMapper.createEntity(request, userId);
        UserGoal savedGoal = userGoalRepository.save(newGoal);
        
        return userGoalMapper.toResponseDTO(savedGoal);
    }
    
    @Transactional
    public UserGoalResponseDTO updateGoal(Long goalId, UserGoalUpdateRequestDTO request, Long userId) {
        userGoalValidator.validateUpdateRequest(request);
        
        UserGoal existingGoal = findGoalByIdAndUserId(goalId, userId);
        userGoalMapper.updateEntity(existingGoal, request);
        
        UserGoal updatedGoal = userGoalRepository.save(existingGoal);
        return userGoalMapper.toResponseDTO(updatedGoal);
    }
    
    @Transactional
    public void deleteGoal(Long goalId, Long userId) {
        UserGoal goal = findGoalByIdAndUserId(goalId, userId);
        goal.setIsActive(false);
        userGoalRepository.save(goal);
    }
    
    public UserGoalResponseDTO getActiveGoal(Long userId) {
        UserGoal activeGoal = userGoalRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new UserGoalNotFoundException("No active goal found for user"));
        return userGoalMapper.toResponseDTO(activeGoal);
    }
    
    public List<UserGoalResponseDTO> getAllUserGoals(Long userId) {
        List<UserGoal> goals = userGoalRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return goals.stream()
                .map(userGoalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    public UserGoalResponseDTO getGoalById(Long goalId, Long userId) {
        UserGoal goal = findGoalByIdAndUserId(goalId, userId);
        return userGoalMapper.toResponseDTO(goal);
    }
    
    public boolean hasActiveGoal(Long userId) {
        return userGoalRepository.existsByUserIdAndIsActiveTrue(userId);
    }
    
    @Transactional
    public UserGoalResponseDTO reactivateGoal(Long goalId, Long userId) {
        UserGoal goalToReactivate = findGoalByIdAndUserId(goalId, userId);
        
        userGoalRepository.deactivateAllActiveGoals(userId);
        
        goalToReactivate.setIsActive(true);
        goalToReactivate.setUpdatedAt(LocalDateTime.now());
        
        UserGoal reactivatedGoal = userGoalRepository.save(goalToReactivate);
        return userGoalMapper.toResponseDTO(reactivatedGoal);
    }
    
    private UserGoal findGoalByIdAndUserId(Long goalId, Long userId) {
        return userGoalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new UserGoalNotFoundException("Goal not found with ID: " + goalId));
    }
}
