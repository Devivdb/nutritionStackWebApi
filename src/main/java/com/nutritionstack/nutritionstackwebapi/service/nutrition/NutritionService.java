package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.dto.product.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.product.ProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.NutritionInfo;
import org.springframework.stereotype.Service;

@Service
public class NutritionService {

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

    private Double getValueOrDefault(Double value) {
        return value != null ? value : 0.0;
    }
}
