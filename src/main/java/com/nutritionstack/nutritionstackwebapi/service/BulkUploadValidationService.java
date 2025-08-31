package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.BulkUploadDataDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.BulkUploadValidationException;
import com.nutritionstack.nutritionstackwebapi.util.ProductValidationUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BulkUploadValidationService {
    
    private final NutritionService nutritionService;
    
    public BulkUploadValidationService(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }
    
    public void validateBulkUploadData(BulkUploadDataDTO uploadData) {
        if (uploadData.getProducts() == null || uploadData.getProducts().isEmpty()) {
            throw new BulkUploadValidationException("Products list cannot be empty");
        }
        
        if (uploadData.getProducts().size() > 1000) {
            throw new BulkUploadValidationException("Cannot upload more than 1000 products at once");
        }
        
        // Validate each product
        for (int i = 0; i < uploadData.getProducts().size(); i++) {
            ProductCreateRequestDTO product = uploadData.getProducts().get(i);
            try {
                validateProduct(product);
            } catch (Exception e) {
                throw new BulkUploadValidationException(
                    "Product at index " + i + " is invalid: " + e.getMessage());
            }
        }
        
        // Check for duplicate EAN13 codes within the upload file
        List<String> ean13Codes = uploadData.getProducts().stream()
                .map(ProductCreateRequestDTO::getEan13Code)
                .collect(Collectors.toList());
        
        List<String> duplicates = ean13Codes.stream()
                .filter(code -> ean13Codes.stream().filter(c -> c.equals(code)).count() > 1)
                .distinct()
                .collect(Collectors.toList());
        
        if (!duplicates.isEmpty()) {
            throw new BulkUploadValidationException("Duplicate EAN13 codes found within the upload file: " + duplicates);
        }
    }
    
    public void validateProduct(ProductCreateRequestDTO product) {
        // Use the enhanced validation utility
        ProductValidationUtil.ValidationResult result = ProductValidationUtil.validateProduct(
            product.getEan13Code(),
            product.getProductName(),
            product.getAmount(),
            product.getUnit() != null ? product.getUnit().toString() : null,
            product.getCalories(),
            product.getProtein(),
            product.getCarbs(),
            product.getFat(),
            product.getFiber(),
            product.getSugar(),
            product.getSalt()
        );
        
        if (!result.isValid()) {
            String errorMessage = "Product validation failed:\n" + 
                String.join("\n", result.getErrors());
            throw new BulkUploadValidationException(errorMessage);
        }
        
        // Apply cleaned values if validation passed
        if (result.getCleanedEan13() != null) {
            product.setEan13Code(result.getCleanedEan13());
        }
        if (result.getCleanedUnit() != null) {
            product.setUnit(result.getCleanedUnit());
        }
    }
    
    public void validateNutritionValues(ProductCreateRequestDTO product) {
        nutritionService.validateNutritionValues(product);
    }
}
