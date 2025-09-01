package com.nutritionstack.nutritionstackwebapi.dto.meal;

import java.time.LocalDateTime;
import java.util.List;

public class MealLogResponseDTO {
    private Long mealId;
    private String mealName;
    private String message;
    private LocalDateTime loggedAt;
    private int productsLogged;
    private List<String> loggedProductDetails;

    public MealLogResponseDTO() {}

    public MealLogResponseDTO(Long mealId, String mealName, String message, LocalDateTime loggedAt, 
                             int productsLogged, List<String> loggedProductDetails) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.message = message;
        this.loggedAt = loggedAt;
        this.productsLogged = productsLogged;
        this.loggedProductDetails = loggedProductDetails;
    }

    public Long getMealId() {
        return mealId;
    }

    public void setMealId(Long mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }

    public int getProductsLogged() {
        return productsLogged;
    }

    public void setProductsLogged(int productsLogged) {
        this.productsLogged = productsLogged;
    }

    public List<String> getLoggedProductDetails() {
        return loggedProductDetails;
    }

    public void setLoggedProductDetails(List<String> loggedProductDetails) {
        this.loggedProductDetails = loggedProductDetails;
    }
}
