package com.nutritionstack.nutritionstackwebapi.dto.tracking;

import com.nutritionstack.nutritionstackwebapi.model.meal.MealType;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.Unit;
import java.time.LocalDateTime;

public class LoggedProductBasicDTO {
    
    private Long id;
    private Long userId;
    private String ean13Code;
    private Double quantity;
    private Unit unit;
    private MealType mealType;
    private LocalDateTime logDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public LoggedProductBasicDTO() {}
    
    public LoggedProductBasicDTO(Long id, Long userId, String ean13Code, Double quantity, 
                                 Unit unit, MealType mealType, LocalDateTime logDate,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.ean13Code = ean13Code;
        this.quantity = quantity;
        this.unit = unit;
        this.mealType = mealType;
        this.logDate = logDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getEan13Code() { return ean13Code; }
    public void setEan13Code(String ean13Code) { this.ean13Code = ean13Code; }
    
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    
    public Unit getUnit() { return unit; }
    public void setUnit(Unit unit) { this.unit = unit; }
    
    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }
    
    public LocalDateTime getLogDate() { return logDate; }
    public void setLogDate(LocalDateTime logDate) { this.logDate = logDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
