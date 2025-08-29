package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.ProductAlreadyExistsException;
import com.nutritionstack.nutritionstackwebapi.exception.ProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.model.NutritionInfo;
import com.nutritionstack.nutritionstackwebapi.model.Product;
import com.nutritionstack.nutritionstackwebapi.model.User;
import com.nutritionstack.nutritionstackwebapi.repository.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    
    public ProductResponseDTO createProduct(ProductCreateRequestDTO request, Long userId) {
        validateProductDoesNotExist(request.getEan13Code());
        
        Product product = new Product();
        product.setEan13Code(request.getEan13Code());
        product.setProductName(request.getProductName());
        product.setAmount(request.getAmount());
        product.setUnit(request.getUnit());
        product.setCreatedBy(userId);
        product.setCreatedAt(LocalDateTime.now());
        
        // Set nutrition info
        NutritionInfo nutritionInfo = new NutritionInfo();
        nutritionInfo.setCalories(request.getCalories());
        nutritionInfo.setProtein(request.getProtein());
        nutritionInfo.setCarbs(request.getCarbs());
        nutritionInfo.setFat(request.getFat());
        nutritionInfo.setFiber(request.getFiber());
        nutritionInfo.setSugar(request.getSugar());
        nutritionInfo.setSalt(request.getSalt());
        product.setNutritionInfo(nutritionInfo);
        
        Product savedProduct = productRepository.save(product);
        return convertToResponseDTO(savedProduct);
    }
    
    public ProductResponseDTO getProductByEan13Code(String ean13Code) {
        Product product = findProductByEan13Code(ean13Code);
        return convertToResponseDTO(product);
    }
    
    public ProductResponseDTO updateProduct(String ean13Code, ProductUpdateRequestDTO request) {
        Product product = findProductByEan13Code(ean13Code);
        
        // Update basic fields
        if (request.getProductName() != null) {
            product.setProductName(request.getProductName());
        }
        if (request.getAmount() != null) {
            product.setAmount(request.getAmount());
        }
        if (request.getUnit() != null) {
            product.setUnit(request.getUnit());
        }
        
        // Update nutrition info
        updateNutritionInfo(product.getNutritionInfo(), request);
        
        Product updatedProduct = productRepository.save(product);
        return convertToResponseDTO(updatedProduct);
    }
    
    public void deleteProduct(String ean13Code) {
        Product product = findProductByEan13Code(ean13Code);
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
    
    private void updateNutritionInfo(NutritionInfo nutritionInfo, ProductUpdateRequestDTO request) {
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
}
