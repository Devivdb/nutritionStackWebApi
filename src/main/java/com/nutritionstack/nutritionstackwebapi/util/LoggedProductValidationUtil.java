package com.nutritionstack.nutritionstackwebapi.util;

import com.nutritionstack.nutritionstackwebapi.model.MealType;
import com.nutritionstack.nutritionstackwebapi.model.Unit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoggedProductValidationUtil {
    
    public static class ValidationResult {
        private final boolean isValid;
        private final List<String> errors;
        
        public ValidationResult(boolean isValid, List<String> errors) {
            this.isValid = isValid;
            this.errors = errors;
        }
        
        public boolean isValid() { return isValid; }
        public List<String> getErrors() { return errors; }
    }
    
    public static ValidationResult validateLoggedProduct(String ean13Code, Double quantity, 
                                                       Unit unit, MealType mealType, LocalDateTime logDate) {
        List<String> errors = new ArrayList<>();
        
        validateEan13Code(ean13Code, errors);
        validateQuantity(quantity, errors);
        validateUnit(unit, errors);
        validateMealType(mealType, errors);
        validateLogDate(logDate, errors);
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    public static ValidationResult validateLoggedProductUpdate(Double quantity, Unit unit, 
                                                             MealType mealType, LocalDateTime logDate) {
        List<String> errors = new ArrayList<>();
        
        // Validate quantity if provided
        if (quantity != null) {
            validateQuantity(quantity, errors);
        }
        
        // Validate unit if provided
        if (unit != null) {
            validateUnit(unit, errors);
        }
        
        // Validate meal type if provided
        if (mealType != null) {
            validateMealType(mealType, errors);
        }
        
        // Validate log date if provided
        if (logDate != null) {
            validateLogDate(logDate, errors);
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    private static void validateEan13Code(String ean13Code, List<String> errors) {
        if (ean13Code == null) {
            errors.add("EAN13 code cannot be null");
            return;
        }
        
        String trimmed = ean13Code.trim();
        
        if (trimmed.isEmpty()) {
            errors.add("EAN13 code cannot be empty");
            return;
        }
        
        if (!trimmed.matches("^\\d{13}$")) {
            errors.add("EAN13 code must be exactly 13 digits");
            return;
        }
    }
    
    private static void validateQuantity(Double quantity, List<String> errors) {
        if (quantity == null) {
            errors.add("Quantity cannot be null");
            return;
        }
        
        if (quantity <= 0) {
            errors.add("Quantity must be greater than 0");
            return;
        }
        
        if (quantity > 999999.99) {
            errors.add("Quantity cannot exceed 999,999.99");
            return;
        }
    }
    
    private static void validateUnit(Unit unit, List<String> errors) {
        if (unit == null) {
            errors.add("Unit cannot be null");
            return;
        }
    }
    
    private static void validateMealType(MealType mealType, List<String> errors) {
        if (mealType == null) {
            errors.add("Meal type cannot be null");
            return;
        }
    }
    
    private static void validateLogDate(LocalDateTime logDate, List<String> errors) {
        if (logDate != null && logDate.isAfter(LocalDateTime.now())) {
            errors.add("Log date cannot be in the future");
            return;
        }
    }
    
    public static void throwValidationExceptionIfInvalid(ValidationResult result, String context) {
        if (!result.isValid()) {
            String errorMessage = context + " validation failed:\n" + 
                String.join("\n", result.getErrors());
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
