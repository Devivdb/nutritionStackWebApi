package com.nutritionstack.nutritionstackwebapi.util;

import com.nutritionstack.nutritionstackwebapi.model.Unit;
import com.nutritionstack.nutritionstackwebapi.exception.BulkUploadValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProductValidationUtil {
    
    private static final Pattern EAN13_PATTERN = Pattern.compile("^\\d{13}$");
    private static final Pattern EAN13_CLEAN_PATTERN = Pattern.compile("^[\\d\\s\\-\\.,]+$");
    
    public static class ValidationResult {
        private final boolean isValid;
        private final List<String> errors;
        private final String cleanedEan13;
        private final Unit cleanedUnit;
        
        public ValidationResult(boolean isValid, List<String> errors, String cleanedEan13, Unit cleanedUnit) {
            this.isValid = isValid;
            this.errors = errors;
            this.cleanedEan13 = cleanedEan13;
            this.cleanedUnit = cleanedUnit;
        }
        
        public boolean isValid() { return isValid; }
        public List<String> getErrors() { return errors; }
        public String getCleanedEan13() { return cleanedEan13; }
        public Unit getCleanedUnit() { return cleanedUnit; }
    }
    
    public static ValidationResult validateProduct(String ean13Code, String productName, Double amount, 
                                                 String unit, Double calories, Double protein, Double carbs, 
                                                 Double fat, Double fiber, Double sugar, Double salt) {
        List<String> errors = new ArrayList<>();
        String cleanedEan13 = null;
        Unit cleanedUnit = null;
        
        // Validate EAN13 code
        ValidationResult ean13Result = Ean13ValidationUtil.validateEan13Code(ean13Code);
        if (!ean13Result.isValid()) {
            errors.addAll(ean13Result.getErrors());
        } else {
            cleanedEan13 = ean13Result.getCleanedEan13();
        }
        
        // Validate product name
        ValidationResult nameResult = validateProductName(productName);
        if (!nameResult.isValid()) {
            errors.addAll(nameResult.getErrors());
        }
        
        // Validate amount
        ValidationResult amountResult = validateAmount(amount);
        if (!amountResult.isValid()) {
            errors.addAll(amountResult.getErrors());
        }
        
        // Validate unit
        ValidationResult unitResult = validateUnit(unit);
        if (!unitResult.isValid()) {
            errors.addAll(unitResult.getErrors());
        } else {
            cleanedUnit = unitResult.getCleanedUnit();
        }
        
        // Validate nutrition values
        ValidationResult nutritionResult = validateNutritionValues(calories, protein, carbs, fat, fiber, sugar, salt);
        if (!nutritionResult.isValid()) {
            errors.addAll(nutritionResult.getErrors());
        }
        
        return new ValidationResult(errors.isEmpty(), errors, cleanedEan13, cleanedUnit);
    }
    
    public static ValidationResult validateProductName(String productName) {
        List<String> errors = new ArrayList<>();
        
        if (productName == null) {
            errors.add("Product name cannot be null");
            return new ValidationResult(false, errors, null, null);
        }
        
        String trimmed = productName.trim();
        
        if (trimmed.isEmpty()) {
            errors.add("Product name cannot be empty");
            return new ValidationResult(false, errors, null, null);
        }
        
        if (trimmed.length() < 2) {
            errors.add("Product name must be at least 2 characters long");
            return new ValidationResult(false, errors, null, null);
        }
        
        if (trimmed.length() > 255) {
            errors.add("Product name cannot exceed 255 characters");
            return new ValidationResult(false, errors, null, null);
        }
        
        return new ValidationResult(true, errors, null, null);
    }
    
    public static ValidationResult validateAmount(Double amount) {
        List<String> errors = new ArrayList<>();
        
        if (amount == null) {
            errors.add("Amount cannot be null");
            return new ValidationResult(false, errors, null, null);
        }
        
        if (amount <= 0) {
            errors.add("Amount must be greater than 0");
            return new ValidationResult(false, errors, null, null);
        }
        
        if (amount > 999999.99) {
            errors.add("Amount cannot exceed 999,999.99");
            return new ValidationResult(false, errors, null, null);
        }
        
        return new ValidationResult(true, errors, null, null);
    }
    
    public static ValidationResult validateUnit(String unit) {
        List<String> errors = new ArrayList<>();
        
        if (unit == null) {
            errors.add("Unit cannot be null");
            return new ValidationResult(false, errors, null, null);
        }
        
        try {
            Unit cleanedUnit = Unit.fromString(unit);
            return new ValidationResult(true, errors, null, cleanedUnit);
        } catch (IllegalArgumentException e) {
            errors.add("Invalid unit: " + e.getMessage());
            return new ValidationResult(false, errors, null, null);
        }
    }
    
    public static ValidationResult validateNutritionValues(Double calories, Double protein, Double carbs, 
                                                         Double fat, Double fiber, Double sugar, Double salt) {
        List<String> errors = new ArrayList<>();
        
        // Validate calories (mandatory)
        if (calories == null) {
            errors.add("Calories is required");
        } else if (calories <= 0) {
            errors.add("Calories must be greater than 0");
        } else if (calories > 9999.99) {
            errors.add("Calories cannot exceed 9,999.99");
        }
        
        // Validate other nutrition values (optional but must be non-negative if provided)
        validateNutritionValue(protein, "Protein", errors);
        validateNutritionValue(carbs, "Carbs", errors);
        validateNutritionValue(fat, "Fat", errors);
        validateNutritionValue(fiber, "Fiber", errors);
        validateNutritionValue(sugar, "Sugar", errors);
        validateNutritionValue(salt, "Salt", errors);
        
        return new ValidationResult(errors.isEmpty(), errors, null, null);
    }
    
    private static void validateNutritionValue(Double value, String fieldName, List<String> errors) {
        if (value != null) {
            if (value < 0) {
                errors.add(fieldName + " must be non-negative");
            } else if (value > 999.99) {
                errors.add(fieldName + " cannot exceed 999.99");
            }
        }
    }
    
    public static void throwValidationExceptionIfInvalid(ValidationResult result, String context) {
        if (!result.isValid()) {
            String errorMessage = context + " validation failed:\n" + 
                String.join("\n", result.getErrors());
            throw new BulkUploadValidationException(errorMessage);
        }
    }
}
