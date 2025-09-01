package com.nutritionstack.nutritionstackwebapi.service.product;

import com.nutritionstack.nutritionstackwebapi.dto.product.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.NutritionInfo;
import com.nutritionstack.nutritionstackwebapi.model.product.Product;
import com.nutritionstack.nutritionstackwebapi.repository.product.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.service.nutrition.NutritionService;
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
        NutritionInfo nutritionInfo = nutritionService.createNutritionInfoWithDefaults(productDto);
        product.setNutritionInfo(nutritionInfo);
        return product;
    }
    

}
