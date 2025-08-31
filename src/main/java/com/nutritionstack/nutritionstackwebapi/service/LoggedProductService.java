package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductBasicDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductInfoDTO;
import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductSimpleResponseDTO;
import com.nutritionstack.nutritionstackwebapi.exception.LoggedProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.exception.ProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.model.LoggedProduct;
import com.nutritionstack.nutritionstackwebapi.model.Product;
import com.nutritionstack.nutritionstackwebapi.repository.LoggedProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.util.LoggedProductValidationUtil;
import com.nutritionstack.nutritionstackwebapi.model.Unit;
import com.nutritionstack.nutritionstackwebapi.model.MealType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoggedProductService {
    
    private final LoggedProductRepository loggedProductRepository;
    private final ProductRepository productRepository;
    private final NutritionCalculationService nutritionCalculationService;
    
    public LoggedProductService(LoggedProductRepository loggedProductRepository, 
                               ProductRepository productRepository,
                               NutritionCalculationService nutritionCalculationService) {
        this.loggedProductRepository = loggedProductRepository;
        this.productRepository = productRepository;
        this.nutritionCalculationService = nutritionCalculationService;
    }
    
    @Transactional
    public LoggedProductResponseDTO logProduct(LoggedProductCreateRequestDTO request, Long userId) {
        validateLoggedProductRequest(request);
        
        Product product = findProductByEan13(request.getEan13Code());
        LoggedProduct loggedProduct = createLoggedProduct(request, userId);
        LoggedProduct savedLoggedProduct = loggedProductRepository.save(loggedProduct);
        
        return buildResponseDTO(savedLoggedProduct, product);
    }
    
    /**
     * ✅ NEW: Simplified version that returns only essential data and calculated nutrition
     */
    @Transactional
    public LoggedProductSimpleResponseDTO logProductSimple(LoggedProductCreateRequestDTO request, Long userId) {
        validateLoggedProductRequest(request);
        
        Product product = findProductByEan13(request.getEan13Code());
        LoggedProduct loggedProduct = createLoggedProduct(request, userId);
        LoggedProduct savedLoggedProduct = loggedProductRepository.save(loggedProduct);
        
        return buildSimpleResponseDTO(savedLoggedProduct, product);
    }
    
    public List<LoggedProductResponseDTO> getUserLoggedProducts(Long userId) {
        List<LoggedProduct> loggedProducts = loggedProductRepository.findByUserIdOrderByLogDateDesc(userId);
        return loggedProducts.stream()
            .map(this::buildResponseDTOWithProduct)
            .collect(Collectors.toList());
    }
    
    /**
     * ✅ NEW: Simplified version that returns only essential data and calculated nutrition
     */
    public List<LoggedProductSimpleResponseDTO> getUserLoggedProductsSimple(Long userId) {
        List<LoggedProduct> loggedProducts = loggedProductRepository.findByUserIdOrderByLogDateDesc(userId);
        return loggedProducts.stream()
            .map(this::buildSimpleResponseDTOWithProduct)
            .collect(Collectors.toList());
    }
    
    public LoggedProductResponseDTO getLoggedProduct(Long logId, Long userId) {
        LoggedProduct loggedProduct = findLoggedProductByIdAndUserId(logId, userId);
        return buildResponseDTOWithProduct(loggedProduct);
    }
    
    /**
     * ✅ NEW: Simplified version that returns only essential data and calculated nutrition
     */
    public LoggedProductSimpleResponseDTO getLoggedProductSimple(Long logId, Long userId) {
        LoggedProduct loggedProduct = findLoggedProductByIdAndUserId(logId, userId);
        return buildSimpleResponseDTOWithProduct(loggedProduct);
    }
    
    @Transactional
    public LoggedProductResponseDTO updateLoggedProduct(Long logId, LoggedProductUpdateRequestDTO request, Long userId) {
        LoggedProduct existingLoggedProduct = findLoggedProductByIdAndUserId(logId, userId);
        validateLoggedProductUpdate(request);
        updateLoggedProductFields(existingLoggedProduct, request);
        
        LoggedProduct updatedLoggedProduct = loggedProductRepository.save(existingLoggedProduct);
        return buildResponseDTOWithProduct(updatedLoggedProduct);
    }
    
    /**
     * ✅ NEW: Simplified version that returns only essential data and calculated nutrition
     */
    @Transactional
    public LoggedProductSimpleResponseDTO updateLoggedProductSimple(Long logId, LoggedProductUpdateRequestDTO request, Long userId) {
        LoggedProduct existingLoggedProduct = findLoggedProductByIdAndUserId(logId, userId);
        validateLoggedProductUpdate(request);
        updateLoggedProductFields(existingLoggedProduct, request);
        
        LoggedProduct updatedLoggedProduct = loggedProductRepository.save(existingLoggedProduct);
        return buildSimpleResponseDTOWithProduct(updatedLoggedProduct);
    }
    
    @Transactional
    public void deleteLoggedProduct(Long logId, Long userId) {
        verifyLoggedProductOwnership(logId, userId);
        loggedProductRepository.deleteById(logId);
    }
    
    public List<LoggedProductResponseDTO> getUserLoggedProductsByDate(Long userId, LocalDateTime date) {
        List<LoggedProduct> loggedProducts = loggedProductRepository.findByUserIdAndLogDateOrderByLogDateDesc(userId, date);
        return loggedProducts.stream()
            .map(this::buildResponseDTOWithProduct)
            .collect(Collectors.toList());
    }
    
    public List<LoggedProductResponseDTO> getUserLoggedProductsByMealType(Long userId, String mealType) {
        // Convert String to MealType enum
        MealType mealTypeEnum = MealType.fromString(mealType);
        List<LoggedProduct> loggedProducts = loggedProductRepository.findByUserIdAndMealTypeOrderByLogDateDesc(userId, mealTypeEnum);
        return loggedProducts.stream()
            .map(this::buildResponseDTOWithProduct)
            .collect(Collectors.toList());
    }
    
    /**
     * ✅ NEW: Simplified versions that return only essential data and calculated nutrition
     */
    public List<LoggedProductSimpleResponseDTO> getUserLoggedProductsByDateSimple(Long userId, LocalDateTime date) {
        List<LoggedProduct> loggedProducts = loggedProductRepository.findByUserIdAndLogDateOrderByLogDateDesc(userId, date);
        return loggedProducts.stream()
            .map(this::buildSimpleResponseDTOWithProduct)
            .collect(Collectors.toList());
    }
    
    public List<LoggedProductSimpleResponseDTO> getUserLoggedProductsByMealTypeSimple(Long userId, String mealType) {
        // Convert String to MealType enum
        MealType mealTypeEnum = MealType.fromString(mealType);
        List<LoggedProduct> loggedProducts = loggedProductRepository.findByUserIdAndMealTypeOrderByLogDateDesc(userId, mealTypeEnum);
        return loggedProducts.stream()
            .map(this::buildSimpleResponseDTOWithProduct)
            .collect(Collectors.toList());
    }
    
    // Private helper methods
    private void validateLoggedProductRequest(LoggedProductCreateRequestDTO request) {
        LoggedProductValidationUtil.ValidationResult validationResult = LoggedProductValidationUtil.validateLoggedProduct(
            request.getEan13Code(),
            request.getQuantity(),
            request.getUnit(),
            request.getMealType(),
            request.getLogDate()
        );
        
        if (!validationResult.isValid()) {
            String errorMessage = "Logged product validation failed:\n" + 
                String.join("\n", validationResult.getErrors());
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    private Product findProductByEan13(String ean13Code) {
        return productRepository.findByEan13Code(ean13Code)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with EAN13 code: " + ean13Code));
    }
    
    private LoggedProduct createLoggedProduct(LoggedProductCreateRequestDTO request, Long userId) {
        LoggedProduct loggedProduct = new LoggedProduct();
        loggedProduct.setUserId(userId);
        loggedProduct.setEan13Code(request.getEan13Code());
        loggedProduct.setQuantity(request.getQuantity());
        loggedProduct.setUnit(request.getUnit());
        loggedProduct.setMealType(request.getMealType());
        loggedProduct.setLogDate(request.getLogDate() != null ? request.getLogDate() : LocalDateTime.now());
        return loggedProduct;
    }
    
    private LoggedProduct findLoggedProductByIdAndUserId(Long logId, Long userId) {
        return loggedProductRepository.findByIdAndUserId(logId, userId)
            .orElseThrow(() -> new LoggedProductNotFoundException("Logged product not found with ID: " + logId));
    }
    
    private void validateLoggedProductUpdate(LoggedProductUpdateRequestDTO request) {
        LoggedProductValidationUtil.ValidationResult validationResult = LoggedProductValidationUtil.validateLoggedProductUpdate(
            request.getQuantity(),
            request.getUnit(),
            request.getMealType(),
            request.getLogDate()
        );
        
        if (!validationResult.isValid()) {
            String errorMessage = "Logged product update validation failed:\n" + 
                String.join("\n", validationResult.getErrors());
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    private void updateLoggedProductFields(LoggedProduct loggedProduct, LoggedProductUpdateRequestDTO request) {
        if (request.getQuantity() != null) {
            loggedProduct.setQuantity(request.getQuantity());
        }
        if (request.getUnit() != null) {
            loggedProduct.setUnit(request.getUnit());
        }
        if (request.getMealType() != null) {
            loggedProduct.setMealType(request.getMealType());
        }
        if (request.getLogDate() != null) {
            loggedProduct.setLogDate(request.getLogDate());
        }
    }
    
    private void verifyLoggedProductOwnership(Long logId, Long userId) {
        if (!loggedProductRepository.existsByIdAndUserId(logId, userId)) {
            throw new LoggedProductNotFoundException("Logged product not found with ID: " + logId);
        }
    }
    
    private LoggedProductResponseDTO buildResponseDTO(LoggedProduct loggedProduct, Product product) {
        LoggedProductBasicDTO loggedProductDTO = convertToBasicDTO(loggedProduct);
        ProductInfoDTO productInfoDTO = convertToProductInfoDTO(product);
        NutritionCalculationService.CalculatedNutrition calculatedNutrition = 
            nutritionCalculationService.calculateNutrition(
                product.getNutritionInfo(),
                product.getAmount(),
                product.getUnit(),
                loggedProduct.getQuantity(),
                loggedProduct.getUnit()
            );
        
        return new LoggedProductResponseDTO(loggedProductDTO, productInfoDTO, calculatedNutrition);
    }
    
    private LoggedProductResponseDTO buildResponseDTOWithProduct(LoggedProduct loggedProduct) {
        Product product = findProductByEan13(loggedProduct.getEan13Code());
        return buildResponseDTO(loggedProduct, product);
    }
    
    private LoggedProductBasicDTO convertToBasicDTO(LoggedProduct loggedProduct) {
        return new LoggedProductBasicDTO(
            loggedProduct.getId(),
            loggedProduct.getUserId(),
            loggedProduct.getEan13Code(),
            loggedProduct.getQuantity(),
            loggedProduct.getUnit(),
            loggedProduct.getMealType(),
            loggedProduct.getLogDate(),
            loggedProduct.getCreatedAt(),
            loggedProduct.getUpdatedAt()
        );
    }
    
    private ProductInfoDTO convertToProductInfoDTO(Product product) {
        return new ProductInfoDTO(
            product.getEan13Code(),
            product.getProductName(),
            product.getAmount(),
            product.getUnit().getValue()
        );
    }
    
    /**
     * ✅ NEW: Build simplified response DTO with essential data and calculated nutrition
     */
    private LoggedProductSimpleResponseDTO buildSimpleResponseDTO(LoggedProduct loggedProduct, Product product) {
        NutritionCalculationService.CalculatedNutrition calculatedNutrition = 
            nutritionCalculationService.calculateNutrition(
                product.getNutritionInfo(),
                product.getAmount(),
                product.getUnit(),
                loggedProduct.getQuantity(),
                loggedProduct.getUnit()
            );
        
        return new LoggedProductSimpleResponseDTO(
            loggedProduct.getId(),
            loggedProduct.getUserId(),
            loggedProduct.getEan13Code(),
            product.getProductName(),
            loggedProduct.getQuantity(),
            loggedProduct.getUnit(),
            loggedProduct.getMealType(),
            loggedProduct.getLogDate(),
            calculatedNutrition.getCalories(),
            calculatedNutrition.getProtein(),
            calculatedNutrition.getCarbs(),
            calculatedNutrition.getFat(),
            calculatedNutrition.getFiber(),
            calculatedNutrition.getSugar(),
            calculatedNutrition.getSalt()
        );
    }
    
    /**
     * ✅ NEW: Build simplified response DTO for existing logged product
     */
    private LoggedProductSimpleResponseDTO buildSimpleResponseDTOWithProduct(LoggedProduct loggedProduct) {
        Product product = findProductByEan13(loggedProduct.getEan13Code());
        return buildSimpleResponseDTO(loggedProduct, product);
    }
}
