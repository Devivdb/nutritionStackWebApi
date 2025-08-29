package com.nutritionstack.nutritionstackwebapi.dto;

import jakarta.validation.constraints.*;

public class ProductUpdateRequestDTO extends BaseNutritionDTO {
    
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String productName;
    
    @DecimalMin(value = "0.1", message = "Amount must be greater than 0")
    private Double amount;
    
    @Size(max = 50, message = "Unit must not exceed 50 characters")
    private String unit;
    
    public ProductUpdateRequestDTO() {}
    
    public ProductUpdateRequestDTO(String productName, Double amount, String unit,
                                   Double calories, Double protein, Double carbs, Double fat, 
                                   Double fiber, Double sugar, Double salt) {
        super(calories, protein, carbs, fat, fiber, sugar, salt);
        this.productName = productName;
        this.amount = amount;
        this.unit = unit;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
