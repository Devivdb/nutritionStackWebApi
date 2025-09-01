package com.nutritionstack.nutritionstackwebapi.service.product;

import com.nutritionstack.nutritionstackwebapi.dto.product.BulkUploadDataDTO;
import com.nutritionstack.nutritionstackwebapi.dto.product.BulkUploadResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.product.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.BulkUploadValidationException;
import com.nutritionstack.nutritionstackwebapi.model.product.BulkUpload;
import com.nutritionstack.nutritionstackwebapi.repository.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BulkUploadService {
    
    private final BulkUploadFileService fileService;
    private final BulkUploadValidationService validationService;
    private final BulkUploadProcessingService processingService;
    private final BulkUploadManagementService managementService;
    private final ProductRepository productRepository;
    
    public BulkUploadService(BulkUploadFileService fileService,
                           BulkUploadValidationService validationService,
                           BulkUploadProcessingService processingService,
                           BulkUploadManagementService managementService,
                           ProductRepository productRepository) {
        this.fileService = fileService;
        this.validationService = validationService;
        this.processingService = processingService;
        this.managementService = managementService;
        this.productRepository = productRepository;
    }
    
    @Transactional
    public BulkUploadResponseDTO createBulkUpload(MultipartFile file, Long userId) {
        fileService.validateUploadedFile(file);
        BulkUploadDataDTO uploadData = fileService.parseJsonFile(file);
        String fileName = fileService.getFileName(file);
        validationService.validateBulkUploadData(uploadData);
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
        if (newCount == 0) {
            throw new BulkUploadValidationException(
                String.format("All %d products in the file already exist in the system. No new products were added.", 
                originalCount));
        }
        BulkUpload bulkUpload = new BulkUpload(fileName, newCount, userId);
        BulkUpload savedBulkUpload = managementService.saveBulkUpload(bulkUpload);
        
        try {
            processingService.processProducts(newProducts, savedBulkUpload.getId(), userId);
            savedBulkUpload.setStatus(BulkUpload.BulkUploadStatus.COMPLETED);

            if (existingEan13Codes.size() > 0) {
                String filteredInfo = String.format("Filtered out %d existing products with EAN13 codes: %s", 
                        existingEan13Codes.size(), existingEan13Codes);
                savedBulkUpload.setErrorMessage(filteredInfo);
            }
            
            managementService.saveBulkUpload(savedBulkUpload);
            
        } catch (Exception e) {
            savedBulkUpload.setStatus(BulkUpload.BulkUploadStatus.FAILED);
            savedBulkUpload.setErrorMessage(e.getMessage());
            managementService.saveBulkUpload(savedBulkUpload);
            throw e;
        }
        
        String username = managementService.getUsernameById(userId);
        return BulkUploadResponseDTO.fromEntityWithCounts(savedBulkUpload, username, originalCount, filteredCount);
    }

    public List<BulkUploadResponseDTO> getAllBulkUploads() {
        return managementService.getAllBulkUploads();
    }
    
    public List<BulkUploadResponseDTO> getBulkUploadsByUser(Long userId) {
        return managementService.getBulkUploadsByUser(userId);
    }
    
    public BulkUploadResponseDTO getBulkUploadById(Long bulkUploadId) {
        return managementService.getBulkUploadById(bulkUploadId);
    }
    
    @Transactional
    public void deleteProductsByBulkUploadId(Long bulkUploadId) {
        managementService.deleteProductsByBulkUploadId(bulkUploadId);
    }
}
