package com.nutritionstack.nutritionstackwebapi.service.product;

import com.nutritionstack.nutritionstackwebapi.dto.product.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.product.ProductResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.product.ProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.ProductValidationException;
import com.nutritionstack.nutritionstackwebapi.exception.ProductAlreadyExistsException;
import com.nutritionstack.nutritionstackwebapi.exception.ProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.exception.UnauthorizedAccessException;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.NutritionInfo;
import com.nutritionstack.nutritionstackwebapi.model.product.Product;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.auth.UserRole;
import com.nutritionstack.nutritionstackwebapi.repository.product.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import com.nutritionstack.nutritionstackwebapi.util.ProductValidationUtil;
import com.nutritionstack.nutritionstackwebapi.service.nutrition.NutritionService;
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
        validateProductDoesNotExist(validationResult.getCleanedEan13());
        Product product = new Product();
        product.setEan13Code(validationResult.getCleanedEan13());
        product.setProductName(request.getProductName());
        product.setAmount(request.getAmount());
        product.setUnit(validationResult.getCleanedUnit());
        product.setCreatedBy(userId);
        product.setCreatedAt(LocalDateTime.now());
        NutritionInfo nutritionInfo = nutritionService.createNutritionInfoWithDefaults(request);
        product.setNutritionInfo(nutritionInfo);
        Product savedProduct = productRepository.save(product);
        return convertToResponseDTO(savedProduct);
    }
    
    public ProductResponseDTO getProductByEan13Code(String ean13Code) {
        Product product = findProductByEan13Code(ean13Code);
        return convertToResponseDTO(product);
    }

    public ProductResponseDTO getProductByEan13CodeWithOwnership(String ean13Code, Long userId) {
        Product product = findProductByEan13Code(ean13Code);
        ProductResponseDTO response = convertToResponseDTO(product);

        boolean isOwner = product.getCreatedBy().equals(userId);
        
        return response;
    }
    
    @Transactional
    public ProductResponseDTO updateProduct(String ean13Code, ProductUpdateRequestDTO request, Long userId) {
        Product existingProduct = productRepository.findByEan13Code(ean13Code)
                .orElseThrow(() -> new ProductValidationException("Product not found with EAN13 code: " + ean13Code));

        verifyProductUpdatePermission(existingProduct, userId);

        ProductValidationUtil.ValidationResult validationResult = ProductValidationUtil.validateProduct(
            ean13Code,
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

        existingProduct.setProductName(request.getProductName());
        existingProduct.setAmount(request.getAmount());
        existingProduct.setUnit(validationResult.getCleanedUnit());

        NutritionInfo nutritionInfo = existingProduct.getNutritionInfo();
        nutritionService.updateNutritionInfo(nutritionInfo, request);
        
        Product savedProduct = productRepository.save(existingProduct);
        return convertToResponseDTO(savedProduct);
    }
    
    public void deleteProduct(String ean13Code) {
        Product product = findProductByEan13Code(ean13Code);
        productRepository.delete(product);
    }

    public void deleteProductWithOwnershipCheck(String ean13Code, Long userId) {
        Product product = findProductByEan13Code(ean13Code);

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

    private void verifyProductUpdatePermission(Product product, Long userId) {
        if (product.getCreatedBy().equals(userId)) {
            return;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }
        throw new UnauthorizedAccessException(
            "Access denied: You can only update products you created. " +
            "Product '" + product.getProductName() + "' was created by another user."
        );
    }
}
