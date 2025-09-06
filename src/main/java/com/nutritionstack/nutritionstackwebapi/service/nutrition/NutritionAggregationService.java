package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.model.nutrition.NutritionInfo;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import com.nutritionstack.nutritionstackwebapi.model.product.Product;
import com.nutritionstack.nutritionstackwebapi.model.tracking.LoggedProduct;
import com.nutritionstack.nutritionstackwebapi.repository.product.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.tracking.LoggedProductRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NutritionAggregationService {
    
    private final LoggedProductRepository loggedProductRepository;
    private final ProductRepository productRepository;
    
    public NutritionAggregationService(LoggedProductRepository loggedProductRepository, 
                                     ProductRepository productRepository) {
        this.loggedProductRepository = loggedProductRepository;
        this.productRepository = productRepository;
    }
    
    public NutritionSummary calculateNutritionSummary(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<LoggedProduct> loggedProducts = loggedProductRepository
            .findByUserIdAndLogDateBetweenOrderByLogDateDesc(userId, startDate, endDate);
        
        if (loggedProducts.isEmpty()) {
            return new NutritionSummary();
        }

        Map<String, List<LoggedProduct>> productsByMealType = loggedProducts.stream()
            .collect(Collectors.groupingBy(lp -> lp.getMealType().getValue()));

        NutritionInfo totalNutrition = calculateTotalNutrition(loggedProducts);

        Map<String, NutritionInfo> nutritionByMealType = productsByMealType.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> calculateTotalNutrition(entry.getValue())
            ));
        
        return new NutritionSummary(totalNutrition, nutritionByMealType, loggedProducts.size());
    }
    
    public ProgressSummary calculateProgressSummary(NutritionSummary nutritionSummary, UserGoal userGoal) {
        if (userGoal == null) {
            return new ProgressSummary();
        }
        
        NutritionInfo total = nutritionSummary.getTotalNutrition();
        
        double caloriesProgress = calculateProgress(total.getCalories(), userGoal.getCaloriesGoal());
        double proteinProgress = calculateProgress(total.getProtein(), userGoal.getProteinGoal());
        double carbsProgress = calculateProgress(total.getCarbs(), userGoal.getCarbsGoal());
        double fatProgress = calculateProgress(total.getFat(), userGoal.getFatGoal());
        
        return new ProgressSummary(caloriesProgress, proteinProgress, carbsProgress, fatProgress);
    }
    
    private NutritionInfo calculateTotalNutrition(List<LoggedProduct> loggedProducts) {
        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalCarbs = 0.0;
        double totalFat = 0.0;
        double totalFiber = 0.0;
        double totalSugar = 0.0;
        double totalSalt = 0.0;
        
        for (LoggedProduct loggedProduct : loggedProducts) {
            Product product = productRepository.findByEan13Code(loggedProduct.getEan13Code())
                .orElse(null);
            
            if (product != null && product.getNutritionInfo() != null) {
                NutritionInfo nutritionInfo = product.getNutritionInfo();
                double multiplier = calculateMultiplier(loggedProduct, product);
                
                totalCalories += (nutritionInfo.getCalories() != null ? nutritionInfo.getCalories() : 0.0) * multiplier;
                totalProtein += (nutritionInfo.getProtein() != null ? nutritionInfo.getProtein() : 0.0) * multiplier;
                totalCarbs += (nutritionInfo.getCarbs() != null ? nutritionInfo.getCarbs() : 0.0) * multiplier;
                totalFat += (nutritionInfo.getFat() != null ? nutritionInfo.getFat() : 0.0) * multiplier;
                totalFiber += (nutritionInfo.getFiber() != null ? nutritionInfo.getFiber() : 0.0) * multiplier;
                totalSugar += (nutritionInfo.getSugar() != null ? nutritionInfo.getSugar() : 0.0) * multiplier;
                totalSalt += (nutritionInfo.getSalt() != null ? nutritionInfo.getSalt() : 0.0) * multiplier;
            }
        }
        
        return new NutritionInfo(totalCalories, totalProtein, totalCarbs, totalFat, 
                               totalFiber, totalSugar, totalSalt);
    }
    
    private double calculateMultiplier(LoggedProduct loggedProduct, Product product) {
        double loggedQuantity = loggedProduct.getQuantity();
        double productAmount = product.getAmount();
        return loggedQuantity / productAmount;
    }
    
    private double calculateProgress(double actual, double goal) {
        if (goal <= 0) return 0.0;
        return Math.min(100.0, (actual / goal) * 100.0);
    }

    public static class NutritionSummary {
        private final NutritionInfo totalNutrition;
        private final Map<String, NutritionInfo> nutritionByMealType;
        private final int totalProducts;
        
        public NutritionSummary() {
            this.totalNutrition = new NutritionInfo(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            this.nutritionByMealType = Map.of();
            this.totalProducts = 0;
        }
        
        public NutritionSummary(NutritionInfo totalNutrition, Map<String, NutritionInfo> nutritionByMealType, int totalProducts) {
            this.totalNutrition = totalNutrition;
            this.nutritionByMealType = nutritionByMealType;
            this.totalProducts = totalProducts;
        }
        
        public NutritionInfo getTotalNutrition() { return totalNutrition; }
        public Map<String, NutritionInfo> getNutritionByMealType() { return nutritionByMealType; }
        public int getTotalProducts() { return totalProducts; }
    }
    
    public static class ProgressSummary {
        private final double caloriesProgress;
        private final double proteinProgress;
        private final double carbsProgress;
        private final double fatProgress;
        
        public ProgressSummary() {
            this.caloriesProgress = 0.0;
            this.proteinProgress = 0.0;
            this.carbsProgress = 0.0;
            this.fatProgress = 0.0;
        }
        
        public ProgressSummary(double caloriesProgress, double proteinProgress, double carbsProgress, double fatProgress) {
            this.caloriesProgress = caloriesProgress;
            this.proteinProgress = proteinProgress;
            this.carbsProgress = carbsProgress;
            this.fatProgress = fatProgress;
        }
        
        public double getCaloriesProgress() { return caloriesProgress; }
        public double getProteinProgress() { return proteinProgress; }
        public double getCarbsProgress() { return carbsProgress; }
        public double getFatProgress() { return fatProgress; }
    }
}
