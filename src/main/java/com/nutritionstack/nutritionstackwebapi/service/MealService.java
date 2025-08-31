package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.MealCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.MealProductDTO;
import com.nutritionstack.nutritionstackwebapi.dto.MealProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.MealResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.MealLogResponseDTO;
import com.nutritionstack.nutritionstackwebapi.model.Meal;
import com.nutritionstack.nutritionstackwebapi.model.MealProduct;
import com.nutritionstack.nutritionstackwebapi.model.MealType;
import com.nutritionstack.nutritionstackwebapi.model.LoggedProduct;
import com.nutritionstack.nutritionstackwebapi.repository.MealRepository;
import com.nutritionstack.nutritionstackwebapi.repository.MealProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.LoggedProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MealService {
    
    private final MealRepository mealRepository;
    private final MealProductRepository mealProductRepository;
    private final LoggedProductRepository loggedProductRepository;
    private final MealValidationService mealValidationService;
    private final MealDtoConverterService mealDtoConverterService;
    
    public MealService(MealRepository mealRepository, MealProductRepository mealProductRepository,
                      LoggedProductRepository loggedProductRepository, MealValidationService mealValidationService,
                      MealDtoConverterService mealDtoConverterService) {
        this.mealRepository = mealRepository;
        this.mealProductRepository = mealProductRepository;
        this.loggedProductRepository = loggedProductRepository;
        this.mealValidationService = mealValidationService;
        this.mealDtoConverterService = mealDtoConverterService;
    }
    
    @Transactional
    public MealResponseDTO createMeal(MealCreateRequestDTO request, Long userId) {
        mealValidationService.validateUserExists(userId);
        mealValidationService.validateProductsExist(request.getProducts());
        
        Meal meal = new Meal(request.getMealName(), request.getMealType(), userId);
        Meal savedMeal = mealRepository.save(meal);
        
        addProductsToMeal(savedMeal, request.getProducts());
        mealRepository.save(savedMeal);
        
        return mealDtoConverterService.convertToResponseDTO(savedMeal);
    }
    
    public MealResponseDTO getMealById(Long mealId, Long userId, boolean isAdmin) {
        Meal meal = mealValidationService.getMealEntityByIdAndUser(mealId, userId, isAdmin);
        return mealDtoConverterService.convertToResponseDTO(meal);
    }
    
    @Transactional
    public MealResponseDTO addProductToMeal(Long mealId, MealProductDTO productDTO, Long userId, boolean isAdmin) {
        Meal meal = mealValidationService.getMealEntityByIdAndUser(mealId, userId, isAdmin);
        mealValidationService.validateProductExists(productDTO.getEan13Code());
        mealValidationService.validateProductNotInMeal(mealId, productDTO.getEan13Code());
        
        MealProduct mealProduct = new MealProduct(meal, productDTO.getEan13Code(), 
                                                productDTO.getQuantity(), productDTO.getUnit());
        meal.addMealProduct(mealProduct);
        
        mealRepository.save(meal);
        return mealDtoConverterService.convertToResponseDTO(meal);
    }
    
    @Transactional
    public MealResponseDTO updateProductQuantity(Long mealId, String ean13Code, 
                                               MealProductUpdateRequestDTO updateDTO, Long userId, boolean isAdmin) {
        Meal meal = mealValidationService.getMealEntityByIdAndUser(mealId, userId, isAdmin);
        MealProduct mealProduct = mealValidationService.getMealProduct(mealId, ean13Code);
        
        mealProduct.setQuantity(updateDTO.getQuantity());
        mealProduct.setUnit(updateDTO.getUnit());
        
        mealProductRepository.save(mealProduct);
        return mealDtoConverterService.convertToResponseDTO(meal);
    }
    
    @Transactional
    public MealResponseDTO removeProductFromMeal(Long mealId, String ean13Code, Long userId, boolean isAdmin) {
        mealValidationService.getMealEntityByIdAndUser(mealId, userId, isAdmin);
        mealValidationService.validateProductInMeal(mealId, ean13Code);
        
        mealProductRepository.deleteByMealIdAndEan13Code(mealId, ean13Code);
        
        Meal refreshedMeal = mealRepository.findById(mealId)
            .orElseThrow(() -> new RuntimeException("Meal not found"));
        return mealDtoConverterService.convertToResponseDTO(refreshedMeal);
    }
    
    @Transactional
    public void deleteMeal(Long mealId, Long userId, boolean isAdmin) {
        Meal meal = mealValidationService.getMealEntityByIdAndUser(mealId, userId, isAdmin);
        mealRepository.delete(meal);
    }
    
    @Transactional
    public MealLogResponseDTO logMeal(Long mealId, Long userId, boolean isAdmin) {
        Meal meal = mealValidationService.getMealEntityByIdAndUser(mealId, userId, isAdmin);
        List<MealProduct> mealProducts = mealProductRepository.findByMealIdOrderByCreatedAt(mealId);
        
        if (mealProducts.isEmpty()) {
            throw new RuntimeException("Cannot log meal with no products");
        }
        
        List<LoggedProduct> loggedProducts = createLoggedProductsFromMeal(mealProducts, userId, meal.getMealType());
        
        List<String> productDetails = loggedProducts.stream()
            .map(lp -> String.format("%s (%.1f %s)", lp.getEan13Code(), lp.getQuantity(), lp.getUnit()))
            .toList();
        
        return new MealLogResponseDTO(
            mealId,
            meal.getMealName(),
            "Meal successfully logged to consumption history",
            LocalDateTime.now(),
            loggedProducts.size(),
            productDetails
        );
    }
    
    // Private helper methods
    private void addProductsToMeal(Meal meal, List<MealProductDTO> products) {
        for (MealProductDTO productDTO : products) {
            MealProduct mealProduct = new MealProduct(meal, productDTO.getEan13Code(), 
                                                    productDTO.getQuantity(), productDTO.getUnit());
            meal.addMealProduct(mealProduct);
        }
    }
    
    private List<LoggedProduct> createLoggedProductsFromMeal(List<MealProduct> mealProducts, Long userId, MealType mealType) {
        List<LoggedProduct> loggedProducts = new ArrayList<>();
        for (MealProduct mealProduct : mealProducts) {
            LoggedProduct loggedProduct = new LoggedProduct(userId, mealProduct.getEan13Code(), 
                                                         mealProduct.getQuantity(), mealProduct.getUnit(), mealType);
            loggedProductRepository.save(loggedProduct);
            loggedProducts.add(loggedProduct);
        }
        return loggedProducts;
    }
}
