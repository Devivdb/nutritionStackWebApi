package com.nutritionstack.nutritionstackwebapi.service.meal;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.BaseNutritionDTO;
import com.nutritionstack.nutritionstackwebapi.dto.meal.MealProductResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.meal.MealResponseDTO;
import com.nutritionstack.nutritionstackwebapi.exception.ProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.model.meal.Meal;
import com.nutritionstack.nutritionstackwebapi.model.meal.MealProduct;
import com.nutritionstack.nutritionstackwebapi.model.product.Product;
import com.nutritionstack.nutritionstackwebapi.repository.product.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealDtoConverterService {
    
    private final ProductRepository productRepository;
    private final MealNutritionService mealNutritionService;
    
    public MealDtoConverterService(ProductRepository productRepository, MealNutritionService mealNutritionService) {
        this.productRepository = productRepository;
        this.mealNutritionService = mealNutritionService;
    }
    
    public MealResponseDTO convertToResponseDTO(Meal meal) {
        List<MealProductResponseDTO> productDTOs = meal.getMealProducts().stream()
            .map(this::convertToProductResponseDTO)
            .collect(Collectors.toList());
        
        BaseNutritionDTO totalNutrition = mealNutritionService.calculateTotalNutrition(productDTOs);
        
        return new MealResponseDTO(
            meal.getId(),
            meal.getMealName(),
            meal.getMealType(),
            meal.getCreatedBy(),
            meal.getCreatedAt(),
            meal.getUpdatedAt(),
            productDTOs,
            totalNutrition
        );
    }
    
    public MealProductResponseDTO convertToProductResponseDTO(MealProduct mealProduct) {
        Product product = productRepository.findByEan13Code(mealProduct.getEan13Code())
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        
        BaseNutritionDTO nutritionDTO = mealNutritionService.calculateScaledNutrition(
            product.getNutritionInfo(), mealProduct.getQuantity(), product.getAmount());
        
        return new MealProductResponseDTO(
            mealProduct.getId(),
            mealProduct.getEan13Code(),
            product.getProductName(),
            mealProduct.getQuantity(),
            mealProduct.getUnit(),
            nutritionDTO
        );
    }
}
