package com.nutritionstack.nutritionstackwebapi.dto;

import com.nutritionstack.nutritionstackwebapi.model.BulkUpload;

import java.time.LocalDateTime;

public class BulkUploadResponseDTO {
    
    private Long id;
    private String fileName;
    private Integer productCount;
    private Integer originalProductCount;
    private Integer filteredProductCount;
    private String uploadedByUsername;
    private LocalDateTime uploadedAt;
    private String status;
    private String errorMessage;
    
    public BulkUploadResponseDTO() {}
    
    public BulkUploadResponseDTO(Long id, String fileName, Integer productCount, 
                                Integer originalProductCount, Integer filteredProductCount,
                                String uploadedByUsername, LocalDateTime uploadedAt, 
                                String status, String errorMessage) {
        this.id = id;
        this.fileName = fileName;
        this.productCount = productCount;
        this.originalProductCount = originalProductCount;
        this.filteredProductCount = filteredProductCount;
        this.uploadedByUsername = uploadedByUsername;
        this.uploadedAt = uploadedAt;
        this.status = status;
        this.errorMessage = errorMessage;
    }
    
    public static BulkUploadResponseDTO fromEntity(BulkUpload bulkUpload, String uploadedByUsername) {
        return new BulkUploadResponseDTO(
                bulkUpload.getId(),
                bulkUpload.getFileName(),
                bulkUpload.getProductCount(),
                bulkUpload.getProductCount(),
                bulkUpload.getProductCount(),
                uploadedByUsername,
                bulkUpload.getUploadedAt(),
                bulkUpload.getStatus().name(),
                bulkUpload.getErrorMessage()
        );
    }
    
    public static BulkUploadResponseDTO fromEntityWithCounts(BulkUpload bulkUpload, String uploadedByUsername, 
                                                           Integer originalCount, Integer filteredCount) {
        return new BulkUploadResponseDTO(
                bulkUpload.getId(),
                bulkUpload.getFileName(),
                bulkUpload.getProductCount(),
                originalCount,
                filteredCount,
                uploadedByUsername,
                bulkUpload.getUploadedAt(),
                bulkUpload.getStatus().name(),
                bulkUpload.getErrorMessage()
        );
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public Integer getProductCount() {
        return productCount;
    }
    
    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }
    
    public Integer getOriginalProductCount() {
        return originalProductCount;
    }
    
    public void setOriginalProductCount(Integer originalProductCount) {
        this.originalProductCount = originalProductCount;
    }
    
    public Integer getFilteredProductCount() {
        return filteredProductCount;
    }
    
    public void setFilteredProductCount(Integer filteredProductCount) {
        this.filteredProductCount = filteredProductCount;
    }
    
    public String getUploadedByUsername() {
        return uploadedByUsername;
    }
    
    public void setUploadedByUsername(String uploadedByUsername) {
        this.uploadedByUsername = uploadedByUsername;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
