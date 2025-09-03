package com.nutritionstack.nutritionstackwebapi.dto.nutrition;

import java.time.LocalDateTime;

public class UserGoalResponseDTO {
    
    private Long id;
    private Long userId;
    private String username;
    private Double caloriesGoal;
    private Double proteinGoal;
    private Double carbsGoal;
    private Double fatGoal;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public UserGoalResponseDTO() {}
    
    public UserGoalResponseDTO(Long id, Long userId, String username, Double caloriesGoal, 
                              Double proteinGoal, Double carbsGoal, Double fatGoal, 
                              Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.caloriesGoal = caloriesGoal;
        this.proteinGoal = proteinGoal;
        this.carbsGoal = carbsGoal;
        this.fatGoal = fatGoal;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
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
