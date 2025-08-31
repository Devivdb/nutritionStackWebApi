package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.ProductValidationException;
import com.nutritionstack.nutritionstackwebapi.exception.ProductAlreadyExistsException;
import com.nutritionstack.nutritionstackwebapi.exception.ProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.exception.UnauthorizedAccessException;
import com.nutritionstack.nutritionstackwebapi.model.NutritionInfo;
import com.nutritionstack.nutritionstackwebapi.model.Product;
import com.nutritionstack.nutritionstackwebapi.model.Unit;
import com.nutritionstack.nutritionstackwebapi.model.User;
import com.nutritionstack.nutritionstackwebapi.model.UserRole;
import com.nutritionstack.nutritionstackwebapi.repository.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.UserRepository;
import com.nutritionstack.nutritionstackwebapi.util.ProductValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NutritionService nutritionService;
    
    public ProductService(ProductRepository productRepository, UserRepository userRepository, NutritionService nutritionService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.nutritionService = nutritionService;
    }
    
    @Transactional
    public ProductResponseDTO createProduct(ProductCreateRequestDTO request, Long userId) {
        // Validate product data using enhanced validation utility
        ProductValidationUtil.ValidationResult validationResult = ProductValidationUtil.validateProduct(
            request.getEan13Code(),
            request.getProductName(),
            request.getAmount(),
            request.getUnit() != null ? request.getUnit().toString() : null,
            request.getCalories(),
            request.getProtein(),
            request.getCarbs(),
            request.getFat(),
            request.getFiber(),
            request.getSugar(),
            request.getSalt()
        );
        
        if (!validationResult.isValid()) {
            String errorMessage = "Product validation failed:\n" + 
                String.join("\n", validationResult.getErrors());
            throw new ProductValidationException(errorMessage);
        }
        
        // Check if product already exists
        validateProductDoesNotExist(validationResult.getCleanedEan13());
        
        // Create product with cleaned values
        Product product = new Product();
        product.setEan13Code(validationResult.getCleanedEan13());
        product.setProductName(request.getProductName());
        product.setAmount(request.getAmount());
        product.setUnit(validationResult.getCleanedUnit());
        product.setCreatedBy(userId);
        product.setCreatedAt(LocalDateTime.now());
        
        // Set nutrition info with defaults for null values
        NutritionInfo nutritionInfo = nutritionService.createNutritionInfoWithDefaults(request);
        product.setNutritionInfo(nutritionInfo);
        
        Product savedProduct = productRepository.save(product);
        return convertToResponseDTO(savedProduct);
    }
    
    public ProductResponseDTO getProductByEan13Code(String ean13Code) {
        Product product = findProductByEan13Code(ean13Code);
        return convertToResponseDTO(product);
    }
    
    /**
     * ✅ SECURITY ENHANCEMENT: Get product with ownership information
     * This allows users to see if they own a product
     */
    public ProductResponseDTO getProductByEan13CodeWithOwnership(String ean13Code, Long userId) {
        Product product = findProductByEan13Code(ean13Code);
        ProductResponseDTO response = convertToResponseDTO(product);
        
        // Add ownership information to response
        boolean isOwner = product.getCreatedBy().equals(userId);
        // Note: You could extend ProductResponseDTO to include isOwner field if needed
        
        return response;
    }
    
    @Transactional
    public ProductResponseDTO updateProduct(String ean13Code, ProductUpdateRequestDTO request, Long userId) {
        Product existingProduct = productRepository.findByEan13Code(ean13Code)
                .orElseThrow(() -> new ProductValidationException("Product not found with EAN13 code: " + ean13Code));
        
        // ✅ SECURITY FIX: Verify ownership or admin role
        verifyProductUpdatePermission(existingProduct, userId);
        
        // Validate update data using enhanced validation utility
        ProductValidationUtil.ValidationResult validationResult = ProductValidationUtil.validateProduct(
            ean13Code, // Use existing EAN13 code for validation
            request.getProductName(),
            request.getAmount(),
            request.getUnit() != null ? request.getUnit().toString() : null,
            request.getCalories(),
            request.getProtein(),
            request.getCarbs(),
            request.getFat(),
            request.getFiber(),
            request.getSugar(),
            request.getSalt()
        );
        
        if (!validationResult.isValid()) {
            String errorMessage = "Product update validation failed:\n" + 
                String.join("\n", validationResult.getErrors());
            throw new ProductValidationException(errorMessage);
        }
        
        // Update product with validated data
        existingProduct.setProductName(request.getProductName());
        existingProduct.setAmount(request.getAmount());
        existingProduct.setUnit(validationResult.getCleanedUnit());
        
        // Update nutrition info with defaults for null values
        NutritionInfo nutritionInfo = existingProduct.getNutritionInfo();
        nutritionService.updateNutritionInfo(nutritionInfo, request);
        
        Product savedProduct = productRepository.save(existingProduct);
        return convertToResponseDTO(savedProduct);
    }
    
    public void deleteProduct(String ean13Code) {
        Product product = findProductByEan13Code(ean13Code);
        productRepository.delete(product);
    }
    
    /**
     * ✅ SECURITY ENHANCEMENT: Add method to delete product with ownership verification
     * This provides an alternative to the admin-only delete endpoint
     */
    public void deleteProductWithOwnershipCheck(String ean13Code, Long userId) {
        Product product = findProductByEan13Code(ean13Code);
        
        // Verify ownership or admin role
        verifyProductUpdatePermission(product, userId);
        
        productRepository.delete(product);
    }
    
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    private void validateProductDoesNotExist(String ean13Code) {
        if (productRepository.existsByEan13Code(ean13Code)) {
            throw new ProductAlreadyExistsException(ean13Code);
        }
    }
    
    private Product findProductByEan13Code(String ean13Code) {
        return productRepository.findByEan13Code(ean13Code)
                .orElseThrow(() -> new ProductNotFoundException(ean13Code));
    }
    

    
    private ProductResponseDTO convertToResponseDTO(Product product) {
        String createdByUsername = getUsernameById(product.getCreatedBy());
        
        return new ProductResponseDTO(
                product.getEan13Code(),
                product.getProductName(),
                product.getAmount(),
                product.getUnit(),
                product.getNutritionInfo().getCalories(),
                product.getNutritionInfo().getProtein(),
                product.getNutritionInfo().getCarbs(),
                product.getNutritionInfo().getFat(),
                product.getNutritionInfo().getFiber(),
                product.getNutritionInfo().getSugar(),
                product.getNutritionInfo().getSalt(),
                createdByUsername,
                product.getCreatedAt(),
                product.getBulkUploadId()
        );
    }
    
    private String getUsernameById(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("Unknown User");
    }
    
    /**
     * ✅ SECURITY METHOD: Verify that a user has permission to update a product
     * Users can only update products they created, unless they are an admin
     */
    private void verifyProductUpdatePermission(Product product, Long userId) {
        // Check if user is the creator of the product
        if (product.getCreatedBy().equals(userId)) {
            return; // User owns the product, allow update
        }
        
        // Check if user is an admin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        if (user.getRole() == UserRole.ADMIN) {
            return; // Admin can update any product
        }
        
        // User is not the owner and not an admin - deny access
        throw new UnauthorizedAccessException(
            "Access denied: You can only update products you created. " +
            "Product '" + product.getProductName() + "' was created by another user."
        );
    }
}
