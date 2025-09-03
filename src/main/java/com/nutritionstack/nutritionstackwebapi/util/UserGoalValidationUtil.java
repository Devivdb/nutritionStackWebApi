package com.nutritionstack.nutritionstackwebapi.util;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.UserGoalUpdateRequestDTO;

public class UserGoalValidationUtil {
    
    private static final double MIN_CALORIES = 800.0;
    private static final double MAX_CALORIES = 10000.0;
    private static final double MIN_PROTEIN_PERCENT = 0.05;
    private static final double MAX_PROTEIN_PERCENT = 0.40;
    private static final double MIN_CARBS_PERCENT = 0.20;
    private static final double MAX_CARBS_PERCENT = 0.65;
    private static final double MIN_FAT_PERCENT = 0.15;
    private static final double MAX_FAT_PERCENT = 0.35;
    
    public static void validateCreateRequest(UserGoalCreateRequestDTO request) {
        validateBasicConstraints(request.getCaloriesGoal(), request.getProteinGoal(), 
                               request.getCarbsGoal(), request.getFatGoal());
        validateMacronutrientRatios(request.getCaloriesGoal(), request.getProteinGoal(), 
                                   request.getCarbsGoal(), request.getFatGoal());
    }
    
    public static void validateUpdateRequest(UserGoalUpdateRequestDTO request) {
        if (request.getCaloriesGoal() != null) {
            validateCaloriesRange(request.getCaloriesGoal());
        }
        if (request.getProteinGoal() != null) {
            validateProteinRange(request.getProteinGoal());
        }
        if (request.getCarbsGoal() != null) {
            validateCarbsRange(request.getCarbsGoal());
        }
        if (request.getFatGoal() != null) {
            validateFatRange(request.getFatGoal());
        }
    }
    
    private static void validateBasicConstraints(Double calories, Double protein, Double carbs, Double fat) {
        validateCaloriesRange(calories);
        validateProteinRange(protein);
        validateCarbsRange(carbs);
        validateFatRange(fat);
    }
    
    private static void validateCaloriesRange(Double calories) {
        if (calories < MIN_CALORIES) {
            throw new IllegalArgumentException("Calories goal must be at least " + MIN_CALORIES + " for basic health");
        }
        if (calories > MAX_CALORIES) {
            throw new IllegalArgumentException("Calories goal cannot exceed " + MAX_CALORIES + " for safety reasons");
        }
    }
    
    private static void validateProteinRange(Double protein) {
        if (protein < 0) {
            throw new IllegalArgumentException("Protein goal cannot be negative");
        }
        if (protein > 400) {
            throw new IllegalArgumentException("Protein goal cannot exceed 400g");
        }
    }
    
    private static void validateCarbsRange(Double carbs) {
        if (carbs < 0) {
            throw new IllegalArgumentException("Carbs goal cannot be negative");
        }
        if (carbs > 1000) {
            throw new IllegalArgumentException("Carbs goal cannot exceed 1000g");
        }
    }
    
    private static void validateFatRange(Double fat) {
        if (fat < 0) {
            throw new IllegalArgumentException("Fat goal cannot be negative");
        }
        if (fat > 200) {
            throw new IllegalArgumentException("Fat goal cannot exceed 200g");
        }
    }
    
    private static void validateMacronutrientRatios(Double calories, Double protein, Double carbs, Double fat) {
        if (calories == null) return;
        
        double proteinCalories = (protein != null ? protein : 0) * 4;
        double carbsCalories = (carbs != null ? carbs : 0) * 4;
        double fatCalories = (fat != null ? fat : 0) * 9;
        double totalMacroCalories = proteinCalories + carbsCalories + fatCalories;
        
        if (totalMacroCalories > calories * 1.2) {
            throw new IllegalArgumentException("Macronutrient goals exceed calorie goal significantly. " +
                    "Total macro calories: " + totalMacroCalories + ", Goal calories: " + calories);
        }
        
        if (protein != null && calories > 0) {
            double proteinPercent = proteinCalories / calories;
            if (proteinPercent < MIN_PROTEIN_PERCENT) {
                throw new IllegalArgumentException("Protein goal is too low. Minimum recommended: " + 
                        (MIN_PROTEIN_PERCENT * 100) + "% of calories");
            }
            if (proteinPercent > MAX_PROTEIN_PERCENT) {
                throw new IllegalArgumentException("Protein goal is too high. Maximum recommended: " + 
                        (MAX_PROTEIN_PERCENT * 100) + "% of calories");
            }
        }
        
        if (carbs != null && calories > 0) {
            double carbsPercent = carbsCalories / calories;
            if (carbsPercent < MIN_CARBS_PERCENT) {
                throw new IllegalArgumentException("Carbs goal is too low. Minimum recommended: " + 
                        (MIN_CARBS_PERCENT * 100) + "% of calories");
            }
            if (carbsPercent > MAX_CARBS_PERCENT) {
                throw new IllegalArgumentException("Carbs goal is too high. Maximum recommended: " + 
                        (MAX_CARBS_PERCENT * 100) + "% of calories");
            }
        }
        
        if (fat != null && calories > 0) {
            double fatPercent = fatCalories / calories;
            if (fatPercent < MIN_FAT_PERCENT) {
                throw new IllegalArgumentException("Fat goal is too low. Minimum recommended: " + 
                        (MIN_FAT_PERCENT * 100) + "% of calories");
            }
            if (fatPercent > MAX_FAT_PERCENT) {
                throw new IllegalArgumentException("Fat goal is too high. Maximum recommended: " + 
                        (MAX_FAT_PERCENT * 100) + "% of calories");
            }
        }
    }
}
