package com.nutritionstack.nutritionstackwebapi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Ean13ValidationUtil {
    
    private static final Pattern EAN13_PATTERN = Pattern.compile("^\\d{13}$");
    private static final Pattern EAN13_CLEAN_PATTERN = Pattern.compile("^[\\d\\s\\-\\.,]+$");
    
    public static ProductValidationUtil.ValidationResult validateEan13Code(String ean13Code) {
        List<String> errors = new ArrayList<>();
        
        if (ean13Code == null) {
            errors.add("EAN13 code cannot be null");
            return new ProductValidationUtil.ValidationResult(false, errors, null, null);
        }
        
        String trimmed = ean13Code.trim();
        
        if (trimmed.isEmpty()) {
            errors.add("EAN13 code cannot be empty");
            return new ProductValidationUtil.ValidationResult(false, errors, null, null);
        }
        
        // Check if it contains only digits, spaces, hyphens, dots, and commas
        if (!EAN13_CLEAN_PATTERN.matcher(trimmed).matches()) {
            // Find the first non-allowed character
            for (int i = 0; i < trimmed.length(); i++) {
                char c = trimmed.charAt(i);
                if (!Character.isDigit(c) && c != ' ' && c != '-' && c != '.' && c != ',') {
                    errors.add("EAN13 code contains invalid character '" + c + "' at position " + (i + 1) + ". Only digits, spaces, hyphens, dots, and commas are allowed");
                    break;
                }
            }
            return new ProductValidationUtil.ValidationResult(false, errors, null, null);
        }
        
        // Remove all non-digit characters
        String cleaned = trimmed.replaceAll("[^\\d]", "");
        
        if (cleaned.length() != 13) {
            if (cleaned.length() < 13) {
                errors.add("EAN13 code '" + trimmed + "' has only " + cleaned.length() + " digits after cleaning. Must be exactly 13 digits");
            } else {
                errors.add("EAN13 code '" + trimmed + "' has " + cleaned.length() + " digits after cleaning. Must be exactly 13 digits");
            }
            return new ProductValidationUtil.ValidationResult(false, errors, null, null);
        }
        
        return new ProductValidationUtil.ValidationResult(true, errors, cleaned, null);
    }
}
