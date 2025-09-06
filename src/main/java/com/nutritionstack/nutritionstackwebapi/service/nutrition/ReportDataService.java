package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import com.nutritionstack.nutritionstackwebapi.model.tracking.LoggedProduct;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import com.nutritionstack.nutritionstackwebapi.repository.tracking.LoggedProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportDataService {
    
    private final UserRepository userRepository;
    private final LoggedProductRepository loggedProductRepository;
    private final UserGoalService userGoalService;
    private final NutritionAggregationService nutritionAggregationService;
    
    public ReportDataService(UserRepository userRepository,
                           LoggedProductRepository loggedProductRepository,
                           UserGoalService userGoalService,
                           NutritionAggregationService nutritionAggregationService) {
        this.userRepository = userRepository;
        this.loggedProductRepository = loggedProductRepository;
        this.userGoalService = userGoalService;
        this.nutritionAggregationService = nutritionAggregationService;
    }
    
    public User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }
    
    public UserGoal getUserGoal(Long userId) {
        try {
            return userGoalService.getActiveGoalEntity(userId);
        } catch (Exception e) {
            return null;
        }
    }
    
    public List<LoggedProduct> getLoggedProducts(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return loggedProductRepository
            .findByUserIdAndLogDateBetweenOrderByLogDateDesc(userId, startDate, endDate);
    }
    
    public NutritionAggregationService.NutritionSummary getNutritionSummary(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return nutritionAggregationService.calculateNutritionSummary(userId, startDate, endDate);
    }
    
    public NutritionAggregationService.ProgressSummary getProgressSummary(
            NutritionAggregationService.NutritionSummary nutritionSummary, UserGoal userGoal) {
        return nutritionAggregationService.calculateProgressSummary(nutritionSummary, userGoal);
    }
}
