package com.nutritionstack.nutritionstackwebapi.model.meal;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MealTypeDeserializer.class)
public enum MealType {
    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    DINNER("dinner"),
    SNACK("snack");
    
    private final String value;
    
    MealType(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    @JsonCreator
    public static MealType fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Meal type cannot be null");
        }
        
        String cleanedText = text.trim().toLowerCase();
        
        for (MealType mealType : MealType.values()) {
            if (mealType.value.equals(cleanedText)) {
                return mealType;
            }
        }
        
        throw new IllegalArgumentException("Unknown meal type: '" + text + "'. Allowed values: breakfast, lunch, dinner, snack");
    }
    
    public static boolean isValidMealType(String text) {
        try {
            fromString(text);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
