package com.nutritionstack.nutritionstackwebapi.service.meal;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.BaseNutritionDTO;
import com.nutritionstack.nutritionstackwebapi.dto.meal.MealProductResponseDTO;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.NutritionInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealNutritionService {

    public BaseNutritionDTO calculateScaledNutrition(NutritionInfo baseNutrition, Double userQuantity, Double baseAmount) {
        double scaleFactor = userQuantity / baseAmount;
        
        return new BaseNutritionDTO(
            scaleNutritionValue(baseNutrition.getCalories(), scaleFactor),
            scaleNutritionValue(baseNutrition.getProtein(), scaleFactor),
            scaleNutritionValue(baseNutrition.getCarbs(), scaleFactor),
            scaleNutritionValue(baseNutrition.getFat(), scaleFactor),
            scaleNutritionValue(baseNutrition.getFiber(), scaleFactor),
            scaleNutritionValue(baseNutrition.getSugar(), scaleFactor),
            scaleNutritionValue(baseNutrition.getSalt(), scaleFactor)
        ) {};
    }

    public BaseNutritionDTO calculateTotalNutrition(List<MealProductResponseDTO> products) {
        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalCarbs = 0.0;
        double totalFat = 0.0;
        double totalFiber = 0.0;
        double totalSugar = 0.0;
        double totalSalt = 0.0;
        
        for (MealProductResponseDTO product : products) {
            BaseNutritionDTO nutrition = product.getNutritionInfo();
            if (nutrition != null) {
                totalCalories += getValueOrDefault(nutrition.getCalories());
                totalProtein += getValueOrDefault(nutrition.getProtein());
                totalCarbs += getValueOrDefault(nutrition.getCarbs());
                totalFat += getValueOrDefault(nutrition.getFat());
                totalFiber += getValueOrDefault(nutrition.getFiber());
                totalSugar += getValueOrDefault(nutrition.getSugar());
                totalSalt += getValueOrDefault(nutrition.getSalt());
            }
        }
        
        return new BaseNutritionDTO(
            roundToTwoDecimals(totalCalories),
            roundToTwoDecimals(totalProtein),
            roundToTwoDecimals(totalCarbs),
            roundToTwoDecimals(totalFat),
            roundToTwoDecimals(totalFiber),
            roundToTwoDecimals(totalSugar),
            roundToTwoDecimals(totalSalt)
        ) {};
    }

    private Double scaleNutritionValue(Double baseValue, double scaleFactor) {
        if (baseValue == null) return 0.0;
        return roundToTwoDecimals(baseValue * scaleFactor);
    }

    private double getValueOrDefault(Double value) {
        return value != null ? value : 0.0;
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
