package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.model.NutritionInfo;
import com.nutritionstack.nutritionstackwebapi.model.Product;
import com.nutritionstack.nutritionstackwebapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BulkUploadProcessingService {
    
    private final ProductRepository productRepository;
    private final NutritionService nutritionService;
    
    public BulkUploadProcessingService(ProductRepository productRepository, NutritionService nutritionService) {
        this.productRepository = productRepository;
        this.nutritionService = nutritionService;
    }
    
    public void processProducts(List<ProductCreateRequestDTO> products, Long bulkUploadId, Long userId) {
        for (ProductCreateRequestDTO productDto : products) {
            Product product = createProductFromDto(productDto, bulkUploadId, userId);
            productRepository.save(product);
        }
    }
    
    private Product createProductFromDto(ProductCreateRequestDTO productDto, Long bulkUploadId, Long userId) {
        Product product = new Product();
        product.setEan13Code(productDto.getEan13Code());
        product.setProductName(productDto.getProductName());
        product.setAmount(productDto.getAmount());
        product.setUnit(productDto.getUnit());
        product.setCreatedBy(userId);
        product.setCreatedAt(LocalDateTime.now());
        product.setBulkUploadId(bulkUploadId);
        
        // Set nutrition info with defaults for null values
        NutritionInfo nutritionInfo = nutritionService.createNutritionInfoWithDefaults(productDto);
        product.setNutritionInfo(nutritionInfo);
        
        return product;
    }
    

}
