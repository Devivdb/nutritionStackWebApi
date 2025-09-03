package com.nutritionstack.nutritionstackwebapi.model.nutrition;

import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_goals")
public class UserGoal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @NotNull(message = "Calories goal is required")
    @DecimalMin(value = "0.1", message = "Calories goal must be greater than 0")
    @Column(nullable = false)
    private Double caloriesGoal;
    
    @DecimalMin(value = "0.0", message = "Protein goal must be non-negative")
    @Column(nullable = false)
    private Double proteinGoal;
    
    @DecimalMin(value = "0.0", message = "Carbs goal must be non-negative")
    @Column(nullable = false)
    private Double carbsGoal;
    
    @DecimalMin(value = "0.0", message = "Fat goal must be non-negative")
    @Column(nullable = false)
    private Double fatGoal;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public UserGoal() {
        this.isActive = true;
    }
    
    public UserGoal(User user, Double caloriesGoal, Double proteinGoal, Double carbsGoal, Double fatGoal) {
        this.user = user;
        this.caloriesGoal = caloriesGoal;
        this.proteinGoal = proteinGoal != null ? proteinGoal : 0.0;
        this.carbsGoal = carbsGoal != null ? carbsGoal : 0.0;
        this.fatGoal = fatGoal != null ? fatGoal : 0.0;
        this.isActive = true;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Double getCaloriesGoal() { return caloriesGoal; }
    public void setCaloriesGoal(Double caloriesGoal) { this.caloriesGoal = caloriesGoal; }
    
    public Double getProteinGoal() { return proteinGoal; }
    public void setProteinGoal(Double proteinGoal) { this.proteinGoal = proteinGoal; }
    
    public Double getCarbsGoal() { return carbsGoal; }
    public void setCarbsGoal(Double carbsGoal) { this.carbsGoal = carbsGoal; }
    
    public Double getFatGoal() { return fatGoal; }
    public void setFatGoal(Double fatGoal) { this.fatGoal = fatGoal; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
