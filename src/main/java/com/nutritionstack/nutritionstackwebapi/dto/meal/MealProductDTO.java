package com.nutritionstack.nutritionstackwebapi.dto.meal;

import jakarta.validation.constraints.*;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.Unit;

public class MealProductDTO {
    
    @NotBlank(message = "EAN13 code is required")
    @Pattern(regexp = "^\\d{13}$", message = "EAN13 code must contain exactly 13 digits (0-9) only")
    private String ean13Code;
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Quantity must not exceed 999999.99")
    private Double quantity;
    
    @NotNull(message = "Unit is required")
    private Unit unit;
    
    public MealProductDTO() {}
    
    public MealProductDTO(String ean13Code, Double quantity, Unit unit) {
        this.ean13Code = ean13Code;
        this.quantity = quantity;
        this.unit = unit;
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
}
