package com.nutritionstack.nutritionstackwebapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meal_products")
public class MealProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;
    
    @NotBlank(message = "EAN13 code is required")
    @Pattern(regexp = "^\\d{13}$", message = "EAN13 code must be exactly 13 digits")
    @Column(name = "ean13_code", length = 13, nullable = false)
    private String ean13Code;
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @Column(name = "quantity", nullable = false)
    private Double quantity;
    
    @NotNull(message = "Unit is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false, length = 20)
    private Unit unit;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public MealProduct() {}
    
    public MealProduct(Meal meal, String ean13Code, Double quantity, Unit unit) {
        this.meal = meal;
        this.ean13Code = ean13Code;
        this.quantity = quantity;
        this.unit = unit;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Meal getMeal() {
        return meal;
    }
    
    public void setMeal(Meal meal) {
        this.meal = meal;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
