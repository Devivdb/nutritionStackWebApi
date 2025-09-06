package com.nutritionstack.nutritionstackwebapi.dto.nutrition;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ReportSummaryDTO {
    
    private Long reportId;
    private String reportName;
    private String reportType;
    private Long fileSize;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime generatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    
    public ReportSummaryDTO() {}
    
    public ReportSummaryDTO(Long reportId, String reportName, String reportType, 
                          Long fileSize, LocalDateTime generatedAt, LocalDateTime startDate, LocalDateTime endDate) {
        this.reportId = reportId;
        this.reportName = reportName;
        this.reportType = reportType;
        this.fileSize = fileSize;
        this.generatedAt = generatedAt;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    
    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}
