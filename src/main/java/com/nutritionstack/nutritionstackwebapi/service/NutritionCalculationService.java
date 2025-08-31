package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.model.NutritionInfo;
import com.nutritionstack.nutritionstackwebapi.model.Unit;
import org.springframework.stereotype.Service;

@Service
public class NutritionCalculationService {
    
    /**
     * Calculate nutrition values for a consumed product based on quantity and unit
     */
    public CalculatedNutrition calculateNutrition(NutritionInfo productNutrition, 
                                                 Double productAmount, 
                                                 Unit productUnit,
                                                 Double consumedQuantity, 
                                                 Unit consumedUnit) {
        
        double conversionFactor = calculateConversionFactor(consumedQuantity, consumedUnit, productAmount, productUnit);
        double nutritionPer100 = 100.0 / productAmount;
        
        return CalculatedNutrition.builder()
            .calories(roundToTwoDecimals(productNutrition.getCalories() * nutritionPer100 * conversionFactor))
            .protein(roundToTwoDecimals(productNutrition.getProtein() * nutritionPer100 * conversionFactor))
            .carbs(roundToTwoDecimals(productNutrition.getCarbs() * nutritionPer100 * conversionFactor))
            .fat(roundToTwoDecimals(productNutrition.getFat() * nutritionPer100 * conversionFactor))
            .fiber(roundToTwoDecimals(productNutrition.getFiber() * nutritionPer100 * conversionFactor))
            .sugar(roundToTwoDecimals(productNutrition.getSugar() * nutritionPer100 * conversionFactor))
            .salt(roundToTwoDecimals(productNutrition.getSalt() * nutritionPer100 * conversionFactor))
            .build();
    }
    
    private double calculateConversionFactor(Double consumedQuantity, Unit consumedUnit, 
                                           Double productAmount, Unit productUnit) {
        if (consumedUnit == productUnit) {
            return consumedQuantity / productAmount;
        }
        
        double consumedInBaseUnit = convertToBaseUnit(consumedQuantity, consumedUnit);
        double productInBaseUnit = convertToBaseUnit(productAmount, productUnit);
        
        return consumedInBaseUnit / productInBaseUnit;
    }
    
    private double convertToBaseUnit(double value, Unit unit) {
        return switch (unit) {
            case G -> value;
            case KG -> value * 1000;
            case ML -> value;
            case L -> value * 1000;
            case OZ -> value * 28.3495;
            case LB -> value * 453.592;
            case CUP -> value * 236.588;
            case TBSP -> value * 14.7868;
            case TSP -> value * 4.92892;
        };
    }
    
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    
    /**
     * Immutable data class for calculated nutrition values
     */
    public static class CalculatedNutrition {
        private final Double calories;
        private final Double protein;
        private final Double carbs;
        private final Double fat;
        private final Double fiber;
        private final Double sugar;
        private final Double salt;
        
        private CalculatedNutrition(Builder builder) {
            this.calories = builder.calories;
            this.protein = builder.protein;
            this.carbs = builder.carbs;
            this.fat = builder.fat;
            this.fiber = builder.fiber;
            this.sugar = builder.sugar;
            this.salt = builder.salt;
        }
        
        // Getters
        public Double getCalories() { return calories; }
        public Double getProtein() { return protein; }
        public Double getCarbs() { return carbs; }
        public Double getFat() { return fat; }
        public Double getFiber() { return fiber; }
        public Double getSugar() { return sugar; }
        public Double getSalt() { return salt; }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private Double calories;
            private Double protein;
            private Double carbs;
            private Double fat;
            private Double fiber;
            private Double sugar;
            private Double salt;
            
            public Builder calories(Double calories) { this.calories = calories; return this; }
            public Builder protein(Double protein) { this.protein = protein; return this; }
            public Builder carbs(Double carbs) { this.carbs = carbs; return this; }
            public Builder fat(Double fat) { this.fat = fat; return this; }
            public Builder fiber(Double fiber) { this.fiber = fiber; return this; }
            public Builder sugar(Double sugar) { this.sugar = sugar; return this; }
            public Builder salt(Double salt) { this.salt = salt; return this; }
            
            public CalculatedNutrition build() {
                return new CalculatedNutrition(this);
            }
        }
    }
}
