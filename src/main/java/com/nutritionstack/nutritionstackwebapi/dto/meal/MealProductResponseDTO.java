package com.nutritionstack.nutritionstackwebapi.dto.meal;

import com.nutritionstack.nutritionstackwebapi.model.nutrition.Unit;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.BaseNutritionDTO;

public class MealProductResponseDTO {
    
    private Long id;
    private String ean13Code;
    private String productName;
    private Double quantity;
    private Unit unit;
    private BaseNutritionDTO nutritionInfo;
    
    public MealProductResponseDTO() {}
    
    public MealProductResponseDTO(Long id, String ean13Code, String productName, 
                                Double quantity, Unit unit, BaseNutritionDTO nutritionInfo) {
        this.id = id;
        this.ean13Code = ean13Code;
        this.productName = productName;
        this.quantity = quantity;
        this.unit = unit;
        this.nutritionInfo = nutritionInfo;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public BaseNutritionDTO getNutritionInfo() {
        return nutritionInfo;
    }
    
    public void setNutritionInfo(BaseNutritionDTO nutritionInfo) {
        this.nutritionInfo = nutritionInfo;
    }
}
