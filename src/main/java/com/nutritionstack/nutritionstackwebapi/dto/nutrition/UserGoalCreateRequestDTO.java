package com.nutritionstack.nutritionstackwebapi.dto.nutrition;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class UserGoalCreateRequestDTO {
    
    @NotNull(message = "Calories goal is required")
    @DecimalMin(value = "0.1", message = "Calories goal must be greater than 0")
    private Double caloriesGoal;
    
    @DecimalMin(value = "0.0", message = "Protein goal must be non-negative")
    private Double proteinGoal;
    
    @DecimalMin(value = "0.0", message = "Carbs goal must be non-negative")
    private Double carbsGoal;
    
    @DecimalMin(value = "0.0", message = "Fat goal must be non-negative")
    private Double fatGoal;
    
    public UserGoalCreateRequestDTO() {}
    
    public UserGoalCreateRequestDTO(Double caloriesGoal, Double proteinGoal, Double carbsGoal, Double fatGoal) {
        this.caloriesGoal = caloriesGoal;
        this.proteinGoal = proteinGoal != null ? proteinGoal : 0.0;
        this.carbsGoal = carbsGoal != null ? carbsGoal : 0.0;
        this.fatGoal = fatGoal != null ? fatGoal : 0.0;
    }

    public Double getCaloriesGoal() { return caloriesGoal; }
    public void setCaloriesGoal(Double caloriesGoal) { this.caloriesGoal = caloriesGoal; }
    
    public Double getProteinGoal() { return proteinGoal; }
    public void setProteinGoal(Double proteinGoal) { this.proteinGoal = proteinGoal; }
    
    public Double getCarbsGoal() { return carbsGoal; }
    public void setCarbsGoal(Double carbsGoal) { this.carbsGoal = carbsGoal; }
    
    public Double getFatGoal() { return fatGoal; }
    public void setFatGoal(Double fatGoal) { this.fatGoal = fatGoal; }
}
