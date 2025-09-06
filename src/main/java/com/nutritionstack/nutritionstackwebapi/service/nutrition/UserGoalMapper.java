package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalResponseDTO;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserGoalMapper {
    
    private final UserRepository userRepository;
    
    public UserGoalMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public UserGoal createEntity(UserGoalCreateRequestDTO request, Long userId) {
        User user = findUserById(userId);
        return new UserGoal(user, request.getCaloriesGoal(), 
                           request.getProteinGoal(), request.getCarbsGoal(), request.getFatGoal());
    }
    
    public void updateEntity(UserGoal existingGoal, UserGoalUpdateRequestDTO request) {
        if (request.getCaloriesGoal() != null) {
            existingGoal.setCaloriesGoal(request.getCaloriesGoal());
        }
        if (request.getProteinGoal() != null) {
            existingGoal.setProteinGoal(request.getProteinGoal());
        }
        if (request.getCarbsGoal() != null) {
            existingGoal.setCarbsGoal(request.getCarbsGoal());
        }
        if (request.getFatGoal() != null) {
            existingGoal.setFatGoal(request.getFatGoal());
        }
    }
    
    public UserGoalResponseDTO toResponseDTO(UserGoal goal) {
        return new UserGoalResponseDTO(
                goal.getId(),
                goal.getUser().getId(),
                goal.getUser().getUsername(),
                goal.getCaloriesGoal(),
                goal.getProteinGoal(),
                goal.getCarbsGoal(),
                goal.getFatGoal(),
                goal.getIsActive(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }
    
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }
}
