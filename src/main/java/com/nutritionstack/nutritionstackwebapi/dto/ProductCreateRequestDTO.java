package com.nutritionstack.nutritionstackwebapi.dto;

import jakarta.validation.constraints.*;

public class ProductCreateRequestDTO extends BaseNutritionDTO {
    
    @NotBlank(message = "EAN13 code is required")
    @Pattern(regexp = "^\\d{13}$", message = "EAN13 code must be exactly 13 digits")
    private String ean13Code;
    
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String productName;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.1", message = "Amount must be greater than 0")
    private Double amount;
    
    @NotBlank(message = "Unit is required")
    @Size(max = 50, message = "Unit must not exceed 50 characters")
    private String unit;
    
    public ProductCreateRequestDTO() {}
    
    public ProductCreateRequestDTO(String ean13Code, String productName, Double amount, String unit,
                                   Double calories, Double protein, Double carbs, Double fat, 
                                   Double fiber, Double sugar, Double salt) {
        super(calories, protein, carbs, fat, fiber, sugar, salt);
        this.ean13Code = ean13Code;
        this.productName = productName;
        this.amount = amount;
        this.unit = unit;
    }
    
    public String getEan13Code() {
        return ean13Code;
    }
    
    public void setEan13Code(String ean13Code) {
        this.ean13Code = ean13Code;
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
