package com.nutritionstack.nutritionstackwebapi.service.meal;

import com.nutritionstack.nutritionstackwebapi.dto.meal.MealProductDTO;
import com.nutritionstack.nutritionstackwebapi.exception.ProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.exception.ResourceNotFoundException;
import com.nutritionstack.nutritionstackwebapi.exception.UnauthorizedAccessException;
import com.nutritionstack.nutritionstackwebapi.exception.ValidationException;
import com.nutritionstack.nutritionstackwebapi.model.meal.Meal;
import com.nutritionstack.nutritionstackwebapi.model.meal.MealProduct;
import com.nutritionstack.nutritionstackwebapi.repository.meal.MealProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.meal.MealRepository;
import com.nutritionstack.nutritionstackwebapi.repository.product.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MealValidationService {
    
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final MealProductRepository mealProductRepository;
    private final MealRepository mealRepository;
    
    public MealValidationService(UserRepository userRepository, ProductRepository productRepository,
                               MealProductRepository mealProductRepository, MealRepository mealRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.mealProductRepository = mealProductRepository;
        this.mealRepository = mealRepository;
    }
    
    public void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UnauthorizedAccessException("User not found");
        }
    }
    
    public void validateProductsExist(List<MealProductDTO> products) {
        for (MealProductDTO product : products) {
            if (!productRepository.existsByEan13Code(product.getEan13Code())) {
                throw new ProductNotFoundException("Product with EAN13 " + product.getEan13Code() + " not found");
            }
        }
    }
    
    public void validateProductExists(String ean13Code) {
        if (!productRepository.existsByEan13Code(ean13Code)) {
            throw new ProductNotFoundException("Product with EAN13 " + ean13Code + " not found");
        }
    }
    
    public void validateProductNotInMeal(Long mealId, String ean13Code) {
        if (mealProductRepository.existsByMealIdAndEan13Code(mealId, ean13Code)) {
            throw new ValidationException("Product already exists in this meal");
        }
    }
    
    public void validateProductInMeal(Long mealId, String ean13Code) {
        if (!mealProductRepository.existsByMealIdAndEan13Code(mealId, ean13Code)) {
            throw new ResourceNotFoundException("Product not found in meal");
        }
    }
    
    public Meal getMealEntityByIdAndUser(Long mealId, Long userId, boolean isAdmin) {
        return mealRepository.findByIdAndCreatedByOrAdmin(mealId, userId, isAdmin)
            .orElseThrow(() -> new ResourceNotFoundException("Meal not found"));
    }
    
    public MealProduct getMealProduct(Long mealId, String ean13Code) {
        return mealProductRepository.findByMealIdAndEan13Code(mealId, ean13Code)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found in meal"));
    }
}
