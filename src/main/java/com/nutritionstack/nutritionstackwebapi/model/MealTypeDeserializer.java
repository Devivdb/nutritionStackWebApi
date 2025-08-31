package com.nutritionstack.nutritionstackwebapi.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public class MealTypeDeserializer extends JsonDeserializer<MealType> {
    
    @Override
    public MealType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        
        if (value == null || value.trim().isEmpty()) {
            throw JsonMappingException.from(p, "Meal type cannot be null or empty");
        }
        
        try {
            return MealType.fromString(value);
        } catch (IllegalArgumentException e) {
            throw JsonMappingException.from(p, "Invalid meal type: '" + value + "'. Allowed values are: breakfast, lunch, dinner, snack");
        }
    }
}
