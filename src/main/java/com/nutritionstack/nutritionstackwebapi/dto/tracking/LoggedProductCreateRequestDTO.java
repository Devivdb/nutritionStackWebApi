package com.nutritionstack.nutritionstackwebapi.dto.tracking;

import com.nutritionstack.nutritionstackwebapi.model.meal.MealType;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.Unit;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class LoggedProductCreateRequestDTO {
    
    @NotBlank(message = "EAN13 code is required")
    @Pattern(regexp = "^\\d{13}$", message = "EAN13 code must be exactly 13 digits")
    private String ean13Code;
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Quantity cannot exceed 999,999.99")
    private Double quantity;
    
    @NotNull(message = "Unit is required")
    private Unit unit;
    
    @NotNull(message = "Meal type is required")
    private MealType mealType;

    private LocalDateTime logDate;
    
    public LoggedProductCreateRequestDTO() {}
    
    public LoggedProductCreateRequestDTO(String ean13Code, Double quantity, Unit unit, MealType mealType) {
        this.ean13Code = ean13Code;
        this.quantity = quantity;
        this.unit = unit;
        this.mealType = mealType;
    }
    
    public LoggedProductCreateRequestDTO(String ean13Code, Double quantity, Unit unit, MealType mealType, LocalDateTime logDate) {
        this.ean13Code = ean13Code;
        this.quantity = quantity;
        this.unit = unit;
        this.mealType = mealType;
        this.logDate = logDate;
    }

    public String getEan13Code() {
        return ean13Code;
    }
    
    public void setEan13Code(String ean13Code) {
        this.ean13Code = ean13Code;
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
