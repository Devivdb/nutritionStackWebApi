package com.nutritionstack.nutritionstackwebapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @NotBlank(message = "EAN13 code is required")
    @Pattern(regexp = "^\\d{13}$", message = "EAN13 code must be exactly 13 digits")
    @Column(name = "ean13_code", length = 13)
    private String ean13Code;
    
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Embedded
    private NutritionInfo nutritionInfo;
    
    @DecimalMin(value = "0.1", message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false)
    private Double amount;
    
    @NotBlank(message = "Unit is required")
    @Size(max = 50, message = "Unit must not exceed 50 characters")
    @Column(name = "unit", nullable = false, length = 50)
    private String unit;
    
    @NotNull(message = "Created by user ID is required")
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "bulk_upload_id")
    private Long bulkUploadId;
    
    public Product() {}
    
    public Product(String ean13Code, String productName, NutritionInfo nutritionInfo, 
                   Double amount, String unit, Long createdBy) {
        this.ean13Code = ean13Code;
        this.productName = productName;
        this.nutritionInfo = nutritionInfo;
        this.amount = amount;
        this.unit = unit;
        this.createdBy = createdBy;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
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
    
    public NutritionInfo getNutritionInfo() {
        return nutritionInfo;
    }
    
    public void setNutritionInfo(NutritionInfo nutritionInfo) {
        this.nutritionInfo = nutritionInfo;
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
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
