package com.nutritionstack.nutritionstackwebapi.dto.tracking;

import com.nutritionstack.nutritionstackwebapi.model.meal.MealType;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.Unit;
import java.time.LocalDateTime;

public class LoggedProductSimpleResponseDTO {
    
    private Long id;
    private Long userId;
    private String ean13Code;
    private String productName;
    private Double quantity;
    private Unit unit;
    private MealType mealType;
    private LocalDateTime logDate;

    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private Double fiber;
    private Double sugar;
    private Double salt;

    public LoggedProductSimpleResponseDTO() {}

    public LoggedProductSimpleResponseDTO(Long id, Long userId, String ean13Code, String productName, 
                                        Double quantity, Unit unit, MealType mealType, LocalDateTime logDate,
                                        Double calories, Double protein, Double carbs, Double fat, 
                                        Double fiber, Double sugar, Double salt) {
        this.id = id;
        this.userId = userId;
        this.ean13Code = ean13Code;
        this.productName = productName;
        this.quantity = quantity;
        this.unit = unit;
        this.mealType = mealType;
        this.logDate = logDate;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
        this.sugar = sugar;
        this.salt = salt;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public Double getCalories() {
        return calories;
    }
    
    public void setCalories(Double calories) {
        this.calories = calories;
    }
    
    public Double getProtein() {
        return protein;
    }
    
    public void setProtein(Double protein) {
        this.protein = protein;
    }
    
    public Double getCarbs() {
        return carbs;
    }
    
    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }
    
    public Double getFat() {
        return fat;
    }
    
    public void setFat(Double fat) {
        this.fat = fat;
    }
    
    public Double getFiber() {
        return fiber;
    }
    
    public void setFiber(Double fiber) {
        this.fiber = fiber;
    }
    
    public Double getSugar() {
        return sugar;
    }
    
    public void setSugar(Double sugar) {
        this.sugar = sugar;
    }
    
    public Double getSalt() {
        return salt;
    }
    
    public void setSalt(Double salt) {
        this.salt = salt;
    }
}
