package com.nutritionstack.nutritionstackwebapi.model.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_uploads")
public class BulkUpload {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name must not exceed 255 characters")
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @NotNull(message = "Product count is required")
    @Positive(message = "Product count must be positive")
    @Column(name = "product_count", nullable = false)
    private Integer productCount;
    
    @NotNull(message = "Uploaded by user ID is required")
    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;
    
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BulkUploadStatus status;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    public enum BulkUploadStatus {
        PROCESSING, COMPLETED, FAILED
    }
    
    public BulkUpload() {}
    
    public BulkUpload(String fileName, Integer productCount, Long uploadedBy) {
        this.fileName = fileName;
        this.productCount = productCount;
        this.uploadedBy = uploadedBy;
        this.status = BulkUploadStatus.PROCESSING;
        this.uploadedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = BulkUploadStatus.PROCESSING;
        }
    }

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
    
    public Long getUploadedBy() {
        return uploadedBy;
    }
    
    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public BulkUploadStatus getStatus() {
        return status;
    }
    
    public void setStatus(BulkUploadStatus status) {
        this.status = status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
