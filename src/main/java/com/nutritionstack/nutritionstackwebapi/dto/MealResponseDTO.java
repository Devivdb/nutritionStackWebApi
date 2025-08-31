package com.nutritionstack.nutritionstackwebapi.dto;

import com.nutritionstack.nutritionstackwebapi.model.MealType;

import java.time.LocalDateTime;
import java.util.List;

public class MealResponseDTO {
    
    private Long id;
    private String mealName;
    private MealType mealType;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MealProductResponseDTO> products;
    private BaseNutritionDTO totalNutrition;
    
    public MealResponseDTO() {}
    
    public MealResponseDTO(Long id, String mealName, MealType mealType, Long createdBy, 
                          LocalDateTime createdAt, LocalDateTime updatedAt, 
                          List<MealProductResponseDTO> products, BaseNutritionDTO totalNutrition) {
        this.id = id;
        this.mealName = mealName;
        this.mealType = mealType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.products = products;
        this.totalNutrition = totalNutrition;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<MealProductResponseDTO> getProducts() {
        return products;
    }
    
    public void setProducts(List<MealProductResponseDTO> products) {
        this.products = products;
    }
    
    public BaseNutritionDTO getTotalNutrition() {
        return totalNutrition;
    }
    
    public void setTotalNutrition(BaseNutritionDTO totalNutrition) {
        this.totalNutrition = totalNutrition;
    }
}
