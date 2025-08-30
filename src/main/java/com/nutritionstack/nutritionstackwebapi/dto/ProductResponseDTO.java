package com.nutritionstack.nutritionstackwebapi.dto;

import java.time.LocalDateTime;
import com.nutritionstack.nutritionstackwebapi.model.Unit;

public class ProductResponseDTO extends BaseNutritionDTO {
    
    private String ean13Code;
    private String productName;
    private Double amount;
    private Unit unit;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private Long bulkUploadId;
    
    public ProductResponseDTO() {}
    
    public ProductResponseDTO(String ean13Code, String productName, Double amount, Unit unit,
                             Double calories, Double protein, Double carbs, Double fat, Double fiber, Double sugar,
                             Double salt, String createdByUsername, LocalDateTime createdAt, Long bulkUploadId) {
        super(calories, protein, carbs, fat, fiber, sugar, salt);
        this.ean13Code = ean13Code;
        this.productName = productName;
        this.amount = amount;
        this.unit = unit;
        this.createdByUsername = createdByUsername;
        this.createdAt = createdAt;
        this.bulkUploadId = bulkUploadId;
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
    
    public Unit getUnit() {
        return unit;
    }
    
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    public String getCreatedByUsername() {
        return createdByUsername;
    }
    
    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Long getBulkUploadId() {
        return bulkUploadId;
    }
    
    public void setBulkUploadId(Long bulkUploadId) {
        this.bulkUploadId = bulkUploadId;
    }
}
