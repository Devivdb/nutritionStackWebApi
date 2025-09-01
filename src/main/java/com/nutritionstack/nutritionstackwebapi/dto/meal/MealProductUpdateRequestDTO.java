package com.nutritionstack.nutritionstackwebapi.dto.meal;

import jakarta.validation.constraints.*;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.Unit;

public class MealProductUpdateRequestDTO {
    

    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Quantity must not exceed 999999.99")
    private Double quantity;
    
    @NotNull(message = "Unit is required")
    private Unit unit;
    
    public MealProductUpdateRequestDTO() {}
    
    public MealProductUpdateRequestDTO(Double quantity, Unit unit) {
        this.quantity = quantity;
        this.unit = unit;
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
