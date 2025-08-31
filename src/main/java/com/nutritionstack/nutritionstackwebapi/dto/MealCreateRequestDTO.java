package com.nutritionstack.nutritionstackwebapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.nutritionstack.nutritionstackwebapi.model.MealType;

import java.util.List;

public class MealCreateRequestDTO {
    
    @NotBlank(message = "Meal name is required")
    @Size(max = 255, message = "Meal name must not exceed 255 characters")
    private String mealName;
    
    @NotNull(message = "Meal type is required")
    private MealType mealType;
    
    @NotEmpty(message = "At least one product is required")
    @Size(max = 50, message = "Meal cannot contain more than 50 products")
    @Valid
    private List<MealProductDTO> products;
    
    public MealCreateRequestDTO() {}
    
    public MealCreateRequestDTO(String mealName, MealType mealType, List<MealProductDTO> products) {
        this.mealName = mealName;
        this.mealType = mealType;
        this.products = products;
    }
    
    // Getters and Setters
    public String getMealName() {
        return mealName;
    }
    
    public void setMealName(String mealName) {
        this.mealName = mealName;
    }
    
    public MealType getMealType() {
        return mealType;
    }
    
    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
    
    public List<MealProductDTO> getProducts() {
        return products;
    }
    
    public void setProducts(List<MealProductDTO> products) {
        this.products = products;
    }
}
