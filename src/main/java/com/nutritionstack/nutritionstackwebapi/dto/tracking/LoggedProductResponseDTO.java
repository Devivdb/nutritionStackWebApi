package com.nutritionstack.nutritionstackwebapi.dto.tracking;

import com.nutritionstack.nutritionstackwebapi.service.nutrition.NutritionCalculationService;
import com.nutritionstack.nutritionstackwebapi.dto.product.ProductInfoDTO;
import java.time.LocalDateTime;

public class LoggedProductResponseDTO {
    
    private LoggedProductBasicDTO loggedProduct;
    private ProductInfoDTO productInfo;
    private NutritionCalculationService.CalculatedNutrition calculatedNutrition;
    
    public LoggedProductResponseDTO() {}
    
    public LoggedProductResponseDTO(LoggedProductBasicDTO loggedProduct, 
                                   ProductInfoDTO productInfo,
                                   NutritionCalculationService.CalculatedNutrition calculatedNutrition) {
        this.loggedProduct = loggedProduct;
        this.productInfo = productInfo;
        this.calculatedNutrition = calculatedNutrition;
    }
    
    public LoggedProductBasicDTO getLoggedProduct() {
        return loggedProduct;
    }
    
    public void setLoggedProduct(LoggedProductBasicDTO loggedProduct) {
        this.loggedProduct = loggedProduct;
    }
    
    public ProductInfoDTO getProductInfo() {
        return productInfo;
    }
    
    public void setProductInfo(ProductInfoDTO productInfo) {
        this.productInfo = productInfo;
    }
    
    public NutritionCalculationService.CalculatedNutrition getCalculatedNutrition() {
        return calculatedNutrition;
    }
    
    public void setCalculatedNutrition(NutritionCalculationService.CalculatedNutrition calculatedNutrition) {
        this.calculatedNutrition = calculatedNutrition;
    }

    public Long getId() { return loggedProduct != null ? loggedProduct.getId() : null; }
    public Long getUserId() { return loggedProduct != null ? loggedProduct.getUserId() : null; }
    public String getEan13Code() { return loggedProduct != null ? loggedProduct.getEan13Code() : null; }
    public Double getQuantity() { return loggedProduct != null ? loggedProduct.getQuantity() : null; }
    public String getUnit() { return loggedProduct != null ? loggedProduct.getUnit() != null ? loggedProduct.getUnit().getValue() : null : null; }
    public String getMealType() { return loggedProduct != null ? loggedProduct.getMealType() != null ? loggedProduct.getMealType().getValue() : null : null; }
    public LocalDateTime getLogDate() { return loggedProduct != null ? loggedProduct.getLogDate() : null; }
    public LocalDateTime getCreatedAt() { return loggedProduct != null ? loggedProduct.getCreatedAt() : null; }
    public LocalDateTime getUpdatedAt() { return loggedProduct != null ? loggedProduct.getUpdatedAt() : null; }
    
    public Double getCalculatedCalories() { return calculatedNutrition != null ? calculatedNutrition.getCalories() : null; }
    public Double getCalculatedProtein() { return calculatedNutrition != null ? calculatedNutrition.getProtein() : null; }
    public Double getCalculatedCarbs() { return calculatedNutrition != null ? calculatedNutrition.getCarbs() : null; }
    public Double getCalculatedFat() { return calculatedNutrition != null ? calculatedNutrition.getFat() : null; }
    public Double getCalculatedFiber() { return calculatedNutrition != null ? calculatedNutrition.getFiber() : null; }
    public Double getCalculatedSugar() { return calculatedNutrition != null ? calculatedNutrition.getSugar() : null; }
    public Double getCalculatedSalt() { return calculatedNutrition != null ? calculatedNutrition.getSalt() : null; }
}
