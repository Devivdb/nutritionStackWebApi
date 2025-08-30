package com.nutritionstack.nutritionstackwebapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutritionstack.nutritionstackwebapi.dto.BulkUploadCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.BulkUploadDataDTO;
import com.nutritionstack.nutritionstackwebapi.dto.BulkUploadResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.BulkUploadValidationException;
import com.nutritionstack.nutritionstackwebapi.model.BulkUpload;
import com.nutritionstack.nutritionstackwebapi.model.NutritionInfo;
import com.nutritionstack.nutritionstackwebapi.model.Product;
import com.nutritionstack.nutritionstackwebapi.model.Unit;
import com.nutritionstack.nutritionstackwebapi.model.User;
import com.nutritionstack.nutritionstackwebapi.repository.BulkUploadRepository;
import com.nutritionstack.nutritionstackwebapi.repository.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.UserRepository;
import com.nutritionstack.nutritionstackwebapi.util.ProductValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BulkUploadService {
    
    private final BulkUploadRepository bulkUploadRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    public BulkUploadService(BulkUploadRepository bulkUploadRepository, 
                           ProductRepository productRepository, 
                           UserRepository userRepository,
                           ObjectMapper objectMapper) {
        this.bulkUploadRepository = bulkUploadRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }
    
    @Transactional
    public BulkUploadResponseDTO createBulkUpload(MultipartFile file, Long userId) {
        // Validate the uploaded file
        validateUploadedFile(file);
        
        // Parse the JSON file
        BulkUploadDataDTO uploadData = parseJsonFile(file);
        
        // Validate the parsed data
        validateBulkUploadData(uploadData);
        
        // Get the original filename
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "unknown_file.json";
        }
        
        // Filter out existing products
        List<String> allEan13Codes = uploadData.getProducts().stream()
                .map(ProductCreateRequestDTO::getEan13Code)
                .collect(Collectors.toList());
        
        List<String> existingEan13Codes = productRepository.findEan13CodesByEan13CodeIn(allEan13Codes);
        List<ProductCreateRequestDTO> newProducts = uploadData.getProducts().stream()
                .filter(product -> !existingEan13Codes.contains(product.getEan13Code()))
                .collect(Collectors.toList());
        
        int originalCount = uploadData.getProducts().size();
        int filteredCount = existingEan13Codes.size();
        int newCount = newProducts.size();
        
        // Check if all products were filtered out before creating the entity
        if (newCount == 0) {
            // All products already exist - this is a conflict
            throw new BulkUploadValidationException(
                String.format("All %d products in the file already exist in the system. No new products were added.", 
                originalCount));
        }
        
        // Create bulk upload record with filtered product count (guaranteed to be > 0)
        BulkUpload bulkUpload = new BulkUpload(fileName, newCount, userId);
        BulkUpload savedBulkUpload = bulkUploadRepository.save(bulkUpload);
        
        try {
            // Process only new products
            processProducts(newProducts, savedBulkUpload.getId(), userId);
            savedBulkUpload.setStatus(BulkUpload.BulkUploadStatus.COMPLETED);

            if (existingEan13Codes.size() > 0) {
                String filteredInfo = String.format("Filtered out %d existing products with EAN13 codes: %s", 
                        existingEan13Codes.size(), existingEan13Codes);
                savedBulkUpload.setErrorMessage(filteredInfo);
            }
            
            bulkUploadRepository.save(savedBulkUpload);
            
        } catch (Exception e) {
            // Update status to failed
            savedBulkUpload.setStatus(BulkUpload.BulkUploadStatus.FAILED);
            savedBulkUpload.setErrorMessage(e.getMessage());
            bulkUploadRepository.save(savedBulkUpload);
            throw e;
        }
        
        String username = getUsernameById(userId);
        return BulkUploadResponseDTO.fromEntityWithCounts(savedBulkUpload, username, originalCount, filteredCount);
    }
    
    public List<BulkUploadResponseDTO> getAllBulkUploads() {
        return bulkUploadRepository.findAllByOrderByUploadedAtDesc().stream()
                .map(upload -> {
                    String username = getUsernameById(upload.getUploadedBy());
                    return BulkUploadResponseDTO.fromEntity(upload, username);
                })
                .collect(Collectors.toList());
    }
    
    public List<BulkUploadResponseDTO> getBulkUploadsByUser(Long userId) {
        return bulkUploadRepository.findByUploadedByOrderByUploadedAtDesc(userId).stream()
                .map(upload -> {
                    String username = getUsernameById(userId);
                    return BulkUploadResponseDTO.fromEntity(upload, username);
                })
                .collect(Collectors.toList());
    }
    
    public BulkUploadResponseDTO getBulkUploadById(Long bulkUploadId) {
        BulkUpload bulkUpload = bulkUploadRepository.findById(bulkUploadId)
                .orElseThrow(() -> new IllegalArgumentException("Bulk upload not found with ID: " + bulkUploadId));
        
        String username = getUsernameById(bulkUpload.getUploadedBy());
        return BulkUploadResponseDTO.fromEntity(bulkUpload, username);
    }
    
    @Transactional
    public void deleteProductsByBulkUploadId(Long bulkUploadId) {
        // Check if bulk upload exists
        if (!bulkUploadRepository.existsById(bulkUploadId)) {
            throw new IllegalArgumentException("Bulk upload not found with ID: " + bulkUploadId);
        }
        
        // Delete all products with this bulk upload ID
        productRepository.deleteByBulkUploadId(bulkUploadId);
    }
    
    private void validateUploadedFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BulkUploadValidationException("No file was uploaded");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new BulkUploadValidationException("File size exceeds maximum limit of 10MB");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/json")) {
            throw new BulkUploadValidationException("Only JSON files are allowed");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".json")) {
            throw new BulkUploadValidationException("File must have .json extension");
        }
    }
    
    private BulkUploadDataDTO parseJsonFile(MultipartFile file) {
        try {
            return objectMapper.readValue(file.getInputStream(), BulkUploadDataDTO.class);
        } catch (IOException e) {
            throw new BulkUploadValidationException("Failed to parse JSON file: " + e.getMessage());
        }
    }
    
    private void validateBulkUploadData(BulkUploadDataDTO uploadData) {
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
    
    private void validateProduct(ProductCreateRequestDTO product) {
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
    
    private void validateNutritionValues(ProductCreateRequestDTO product) {
        if (product.getCalories() != null && product.getCalories() < 0) {
            throw new IllegalArgumentException("Calories cannot be negative");
        }
        if (product.getProtein() != null && product.getProtein() < 0) {
            throw new IllegalArgumentException("Protein cannot be negative");
        }
        if (product.getCarbs() != null && product.getCarbs() < 0) {
            throw new IllegalArgumentException("Carbs cannot be negative");
        }
        if (product.getFat() != null && product.getFat() < 0) {
            throw new IllegalArgumentException("Fat cannot be negative");
        }
        if (product.getFiber() != null && product.getFiber() < 0) {
            throw new IllegalArgumentException("Fiber cannot be negative");
        }
        if (product.getSugar() != null && product.getSugar() < 0) {
            throw new IllegalArgumentException("Sugar cannot be negative");
        }
        if (product.getSalt() != null && product.getSalt() < 0) {
            throw new IllegalArgumentException("Salt cannot be negative");
        }
    }
    
    private void processProducts(List<ProductCreateRequestDTO> products, Long bulkUploadId, Long userId) {
        for (ProductCreateRequestDTO productDto : products) {
            Product product = new Product();
            product.setEan13Code(productDto.getEan13Code());
            product.setProductName(productDto.getProductName());
            product.setAmount(productDto.getAmount());
            product.setUnit(productDto.getUnit());
            product.setCreatedBy(userId);
            product.setCreatedAt(LocalDateTime.now());
            product.setBulkUploadId(bulkUploadId);
            
            // Set nutrition info with defaults for null values
            NutritionInfo nutritionInfo = new NutritionInfo();
            nutritionInfo.setCalories(productDto.getCalories());
            nutritionInfo.setProtein(productDto.getProtein() != null ? productDto.getProtein() : 0.0);
            nutritionInfo.setCarbs(productDto.getCarbs() != null ? productDto.getCarbs() : 0.0);
            nutritionInfo.setFat(productDto.getFat() != null ? productDto.getFat() : 0.0);
            nutritionInfo.setFiber(productDto.getFiber() != null ? productDto.getFiber() : 0.0);
            nutritionInfo.setSugar(productDto.getSugar() != null ? productDto.getSugar() : 0.0);
            nutritionInfo.setSalt(productDto.getSalt() != null ? productDto.getSalt() : 0.0);
            product.setNutritionInfo(nutritionInfo);
            
            productRepository.save(product);
        }
    }
    
    private String getUsernameById(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("Unknown User");
    }
}
