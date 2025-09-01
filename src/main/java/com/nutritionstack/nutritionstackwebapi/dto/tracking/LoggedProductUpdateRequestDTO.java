package com.nutritionstack.nutritionstackwebapi.dto.tracking;

import com.nutritionstack.nutritionstackwebapi.model.meal.MealType;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.Unit;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class LoggedProductUpdateRequestDTO {
    
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Quantity cannot exceed 999,999.99")
    private Double quantity;
    
    private Unit unit;
    
    private MealType mealType;
    
    @PastOrPresent(message = "Log date cannot be in the future")
    private LocalDateTime logDate;
    
    public LoggedProductUpdateRequestDTO() {}
    
    public LoggedProductUpdateRequestDTO(Double quantity, Unit unit, MealType mealType, LocalDateTime logDate) {
        this.quantity = quantity;
        this.unit = unit;
        this.mealType = mealType;
        this.logDate = logDate;
    }

    public Double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    
    public Unit getUnit() {
        return unit;
    }
    
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    public MealType getMealType() {
        return mealType;
    }
    
    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
    
    public LocalDateTime getLogDate() {
        return logDate;
    }
    
    public void setLogDate(LocalDateTime logDate) {
        this.logDate = logDate;
    }
}
