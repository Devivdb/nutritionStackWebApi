package com.nutritionstack.nutritionstackwebapi.dto.nutrition;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class ReportGenerationRequestDTO {
    
    @NotNull(message = "Report type is required")
    @Pattern(regexp = "^(DAY|WEEK|MONTHLY|CUSTOM)$", message = "Report type must be DAY, WEEK, MONTHLY, or CUSTOM")
    private String reportType;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    
    public ReportGenerationRequestDTO() {}
    
    public ReportGenerationRequestDTO(String reportType, LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
        this.reportType = reportType;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}
