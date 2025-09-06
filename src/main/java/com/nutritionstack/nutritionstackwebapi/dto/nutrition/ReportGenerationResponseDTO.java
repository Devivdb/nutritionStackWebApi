package com.nutritionstack.nutritionstackwebapi.dto.nutrition;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ReportGenerationResponseDTO {
    
    private Long reportId;
    private String reportName;
    private String reportType;
    private String message;
    private String downloadUrl;
    private Long fileSize;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime generatedAt;
    
    public ReportGenerationResponseDTO() {}
    
    public ReportGenerationResponseDTO(Long reportId, String reportName, String reportType, 
                                     String message, String downloadUrl, Long fileSize, LocalDateTime generatedAt) {
        this.reportId = reportId;
        this.reportName = reportName;
        this.reportType = reportType;
        this.message = message;
        this.downloadUrl = downloadUrl;
        this.fileSize = fileSize;
        this.generatedAt = generatedAt;
    }
    
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    
    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
