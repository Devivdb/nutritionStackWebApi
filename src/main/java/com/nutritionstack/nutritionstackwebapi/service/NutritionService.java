package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.model.NutritionInfo;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling nutrition-related operations.
 * Centralizes nutrition logic to eliminate duplication across services.
 */
@Service
public class NutritionService {
    
    /**
     * Creates a new NutritionInfo object with consistent defaults for null values.
     * Used when creating new products from DTOs.
     * 
     * @param productDto the product creation request DTO
     * @return NutritionInfo with all fields set (defaults for null nutrition values)
     */
    public NutritionInfo createNutritionInfoWithDefaults(ProductCreateRequestDTO productDto) {
        NutritionInfo nutritionInfo = new NutritionInfo();
        nutritionInfo.setCalories(productDto.getCalories());
        nutritionInfo.setProtein(getValueOrDefault(productDto.getProtein()));
        nutritionInfo.setCarbs(getValueOrDefault(productDto.getCarbs()));
        nutritionInfo.setFat(getValueOrDefault(productDto.getFat()));
        nutritionInfo.setFiber(getValueOrDefault(productDto.getFiber()));
        nutritionInfo.setSugar(getValueOrDefault(productDto.getSugar()));
        nutritionInfo.setSalt(getValueOrDefault(productDto.getSalt()));
        return nutritionInfo;
    }
    
    /**
     * Updates an existing NutritionInfo object with new values from an update request.
     * Only updates fields that are not null in the request, preserving existing values.
     * 
     * @param nutritionInfo the existing nutrition info to update
     * @param request the update request containing new values
     */
    public void updateNutritionInfo(NutritionInfo nutritionInfo, ProductUpdateRequestDTO request) {
        if (request.getCalories() != null) {
            nutritionInfo.setCalories(request.getCalories());
        }
        if (request.getProtein() != null) {
            nutritionInfo.setProtein(request.getProtein());
        }
        if (request.getCarbs() != null) {
            nutritionInfo.setCarbs(request.getCarbs());
        }
        if (request.getFat() != null) {
            nutritionInfo.setFat(request.getFat());
        }
        if (request.getFiber() != null) {
            nutritionInfo.setFiber(request.getFiber());
        }
        if (request.getSugar() != null) {
            nutritionInfo.setSugar(request.getSugar());
        }
        if (request.getSalt() != null) {
            nutritionInfo.setSalt(request.getSalt());
        }
    }
    
    /**
     * Creates a new NutritionInfo object with defaults for null values.
     * Alternative method that takes individual parameters instead of DTO.
     * 
     * @param calories the calories value
     * @param protein the protein value
     * @param carbs the carbs value
     * @param fat the fat value
     * @param fiber the fiber value
     * @param sugar the sugar value
     * @param salt the salt value
     * @return NutritionInfo with all fields set (defaults for null values)
     */
    public NutritionInfo createNutritionInfoWithDefaults(Double calories, Double protein, Double carbs, 
                                                       Double fat, Double fiber, Double sugar, Double salt) {
        NutritionInfo nutritionInfo = new NutritionInfo();
        nutritionInfo.setCalories(calories);
        nutritionInfo.setProtein(getValueOrDefault(protein));
        nutritionInfo.setCarbs(getValueOrDefault(carbs));
        nutritionInfo.setFat(getValueOrDefault(fat));
        nutritionInfo.setFiber(getValueOrDefault(fiber));
        nutritionInfo.setSugar(getValueOrDefault(sugar));
        nutritionInfo.setSalt(getValueOrDefault(salt));
        return nutritionInfo;
    }
    
    /**
     * Validates that nutrition values are not negative.
     * Throws IllegalArgumentException if any value is negative.
     * 
     * @param product the product to validate
     * @throws IllegalArgumentException if any nutrition value is negative
     */
    public void validateNutritionValues(ProductCreateRequestDTO product) {
        if (product.getCalories() != null && product.getCalories() < 0) {
            throw new IllegalArgumentException("Calories cannot be negative");
        }
        if (product.getProtein() != null && product.getProtein() < 0) {
            throw new IllegalArgumentException("Protein cannot be negative");
        }
        if (product.getCarbs() != null && product.getCarbs() < 0) {
            throw new IllegalArgumentException("Carbs cannot be negative");
        }
        if (product.getFat() != null && product.getFat() < 0) {
            throw new IllegalArgumentException("Fat cannot be negative");
        }
        if (product.getFiber() != null && product.getFiber() < 0) {
            throw new IllegalArgumentException("Fiber cannot be negative");
        }
        if (product.getSugar() != null && product.getSugar() < 0) {
            throw new IllegalArgumentException("Sugar cannot be negative");
        }
        if (product.getSalt() != null && product.getSalt() < 0) {
            throw new IllegalArgumentException("Salt cannot be negative");
        }
    }
    
    /**
     * Helper method to get a value or return the default (0.0) if the value is null.
     * 
     * @param value the value to check
     * @return the value if not null, otherwise 0.0
     */
    private Double getValueOrDefault(Double value) {
        return value != null ? value : 0.0;
    }
}
