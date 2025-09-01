package com.nutritionstack.nutritionstackwebapi.service.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutritionstack.nutritionstackwebapi.dto.product.BulkUploadDataDTO;
import com.nutritionstack.nutritionstackwebapi.exception.BulkUploadValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class BulkUploadFileService {
    
    private final ObjectMapper objectMapper;
    
    public BulkUploadFileService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void validateUploadedFile(MultipartFile file) {
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
    
    public BulkUploadDataDTO parseJsonFile(MultipartFile file) {
        try {
            return objectMapper.readValue(file.getInputStream(), BulkUploadDataDTO.class);
        } catch (IOException e) {
            throw new BulkUploadValidationException("Failed to parse JSON file: " + e.getMessage());
        }
    }
    
    public String getFileName(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "unknown_file.json";
        }
        return fileName;
    }
}
