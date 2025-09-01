package com.nutritionstack.nutritionstackwebapi.model.nutrition;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;

@Embeddable
public class NutritionInfo {
    
    @jakarta.validation.constraints.NotNull(message = "Calories is required")
    @jakarta.validation.constraints.DecimalMin(value = "0.1", message = "Calories must be greater than 0")
    private Double calories;
    
    @DecimalMin(value = "0.0", message = "Protein must be non-negative")
    private Double protein;
    
    @DecimalMin(value = "0.0", message = "Carbs must be non-negative")
    private Double carbs;
    
    @DecimalMin(value = "0.0", message = "Fat must be non-negative")
    private Double fat;
    
    @DecimalMin(value = "0.0", message = "Fiber must be non-negative")
    private Double fiber;
    
    @DecimalMin(value = "0.0", message = "Sugar must be non-negative")
    private Double sugar;
    
    @DecimalMin(value = "0.0", message = "Salt must be non-negative")
    private Double salt;
    
    public NutritionInfo() {}
    
    public NutritionInfo(Double calories, Double protein, Double carbs, Double fat, 
                        Double fiber, Double sugar, Double salt) {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
        this.sugar = sugar;
        this.salt = salt;
    }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }
    
    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }
    
    public Double getCarbs() { return carbs; }
    public void setCarbs(Double carbs) { this.carbs = carbs; }
    
    public Double getFat() { return fat; }
    public void setFat(Double fat) { this.fat = fat; }
    
    public Double getFiber() { return fiber; }
    public void setFiber(Double fiber) { this.fiber = fiber; }
    
    public Double getSugar() { return sugar; }
    public void setSugar(Double sugar) { this.sugar = sugar; }
    
    public Double getSalt() { return salt; }
    public void setSalt(Double salt) { this.salt = salt; }
}
