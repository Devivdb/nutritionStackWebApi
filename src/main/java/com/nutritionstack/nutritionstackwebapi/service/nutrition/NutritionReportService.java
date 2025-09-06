package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.ReportGenerationRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.ReportGenerationResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.ReportSummaryDTO;
import com.nutritionstack.nutritionstackwebapi.exception.ResourceNotFoundException;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.NutritionReport;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import com.nutritionstack.nutritionstackwebapi.model.tracking.LoggedProduct;
import com.nutritionstack.nutritionstackwebapi.repository.nutrition.NutritionReportRepository;
import com.nutritionstack.nutritionstackwebapi.util.validation.ReportValidationService;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.ReportType;
import com.nutritionstack.nutritionstackwebapi.service.nutrition.PdfReportRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NutritionReportService {
    
    private final NutritionReportRepository nutritionReportRepository;
    private final PdfReportGeneratorService pdfReportGeneratorService;
    private final ReportValidationService reportValidationService;
    private final ReportFileService reportFileService;
    private final ReportDataService reportDataService;
    
    public NutritionReportService(NutritionReportRepository nutritionReportRepository,
                                PdfReportGeneratorService pdfReportGeneratorService,
                                ReportValidationService reportValidationService,
                                ReportFileService reportFileService,
                                ReportDataService reportDataService) {
        this.nutritionReportRepository = nutritionReportRepository;
        this.pdfReportGeneratorService = pdfReportGeneratorService;
        this.reportValidationService = reportValidationService;
        this.reportFileService = reportFileService;
        this.reportDataService = reportDataService;
    }
    
    public byte[] generateReportPdf(Long userId, ReportGenerationRequestDTO request) {
        com.nutritionstack.nutritionstackwebapi.util.validation.ValidationResult validationResult = 
            reportValidationService.validateReportRequest(
                request.getReportType(), request.getDate(), request.getStartDate(), request.getEndDate());
        
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Report validation failed: " + String.join(", ", validationResult.getErrors()));
        }
        
        ReportType reportType = ReportType.fromString(request.getReportType());
        LocalDateTime[] dateRange = reportValidationService.calculateDateRange(
            reportType, request.getDate(), request.getStartDate(), request.getEndDate());
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];
        
        User user = reportDataService.getUser(userId);
        UserGoal userGoal = reportDataService.getUserGoal(userId);
        List<LoggedProduct> loggedProducts = reportDataService.getLoggedProducts(userId, startDate, endDate);
        
        if (loggedProducts.isEmpty()) {
            System.out.println("Warning: No logged products found for the specified date range, generating empty report");
        }
        
        NutritionAggregationService.NutritionSummary nutritionSummary = 
            reportDataService.getNutritionSummary(userId, startDate, endDate);
        
        NutritionAggregationService.ProgressSummary progressSummary = 
            reportDataService.getProgressSummary(nutritionSummary, userGoal);
        
        try {
            String fileName = reportFileService.generateFileName(userId, request.getReportType(), startDate, endDate);
            String filePath = reportFileService.saveReportToFile(fileName, new byte[0]);
            
            String reportName = reportFileService.generateReportName(request.getReportType(), startDate, endDate);
            NutritionReport report = new NutritionReport(user, reportName, startDate, endDate, 
                request.getReportType(), filePath, 0L);
            
            NutritionReport savedReport = nutritionReportRepository.save(report);
            
            PdfReportRequest pdfRequest = PdfReportRequest.builder()
                .user(user)
                .userGoal(userGoal)
                .nutritionSummary(nutritionSummary)
                .progressSummary(progressSummary)
                .loggedProducts(loggedProducts)
                .startDate(startDate)
                .endDate(endDate)
                .reportType(request.getReportType())
                .reportId(savedReport.getId())
                .build();
                
            byte[] pdfContent = pdfReportGeneratorService.generateNutritionReport(pdfRequest);
            
            reportFileService.saveReportToFile(fileName, pdfContent);
            
            savedReport.setFileSize((long) pdfContent.length);
            nutritionReportRepository.save(savedReport);
            
            return pdfContent;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }
    
    public ReportGenerationResponseDTO generateReport(Long userId, ReportGenerationRequestDTO request) {
        com.nutritionstack.nutritionstackwebapi.util.validation.ValidationResult validationResult = 
            reportValidationService.validateReportRequest(
                request.getReportType(), request.getDate(), request.getStartDate(), request.getEndDate());
        
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Report validation failed: " + String.join(", ", validationResult.getErrors()));
        }
        
        ReportType reportType = ReportType.fromString(request.getReportType());
        LocalDateTime[] dateRange = reportValidationService.calculateDateRange(
            reportType, request.getDate(), request.getStartDate(), request.getEndDate());
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];
        
        User user = reportDataService.getUser(userId);
        UserGoal userGoal = reportDataService.getUserGoal(userId);
        List<LoggedProduct> loggedProducts = reportDataService.getLoggedProducts(userId, startDate, endDate);
        
        if (loggedProducts.isEmpty()) {
            throw new IllegalArgumentException("No logged products found for the specified date range");
        }
        
        NutritionAggregationService.NutritionSummary nutritionSummary = 
            reportDataService.getNutritionSummary(userId, startDate, endDate);
        
        NutritionAggregationService.ProgressSummary progressSummary = 
            reportDataService.getProgressSummary(nutritionSummary, userGoal);
        
        try {
            String fileName = reportFileService.generateFileName(userId, request.getReportType(), startDate, endDate);
            String filePath = reportFileService.saveReportToFile(fileName, new byte[0]);
            
            String reportName = reportFileService.generateReportName(request.getReportType(), startDate, endDate);
            NutritionReport report = new NutritionReport(user, reportName, startDate, endDate, 
                request.getReportType(), filePath, 0L);
            
            NutritionReport savedReport = nutritionReportRepository.save(report);
            
            PdfReportRequest pdfRequest = PdfReportRequest.builder()
                .user(user)
                .userGoal(userGoal)
                .nutritionSummary(nutritionSummary)
                .progressSummary(progressSummary)
                .loggedProducts(loggedProducts)
                .startDate(startDate)
                .endDate(endDate)
                .reportType(request.getReportType())
                .reportId(savedReport.getId())
                .build();
                
            byte[] pdfContent = pdfReportGeneratorService.generateNutritionReport(pdfRequest);
            
            reportFileService.saveReportToFile(fileName, pdfContent);
            
            savedReport.setFileSize((long) pdfContent.length);
            nutritionReportRepository.save(savedReport);
            
            return new ReportGenerationResponseDTO(
                savedReport.getId(),
                savedReport.getReportName(),
                savedReport.getReportType(),
                "Report generated successfully",
                "/api/nutrition-reports/" + savedReport.getId() + "/download",
                savedReport.getFileSize(),
                savedReport.getGeneratedAt()
            );
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }
    
    public List<ReportSummaryDTO> getUserReports(Long userId) {
        List<NutritionReport> reports = nutritionReportRepository.findByUserIdOrderByGeneratedAtDesc(userId);
        return reports.stream()
            .map(this::mapToReportSummaryDTO)
            .collect(Collectors.toList());
    }
    
    public ReportSummaryDTO getReportById(Long reportId, Long userId) {
        NutritionReport report = nutritionReportRepository.findByIdAndUserId(reportId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + reportId));
        return mapToReportSummaryDTO(report);
    }
    
    public byte[] downloadReport(Long reportId, Long userId) {
        NutritionReport report = nutritionReportRepository.findByIdAndUserId(reportId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + reportId));
        
        try {
            Path filePath = Paths.get(report.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read report file", e);
        }
    }
    
    public void deleteReport(Long reportId, Long userId) {
        NutritionReport report = nutritionReportRepository.findByIdAndUserId(reportId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + reportId));
        
        reportFileService.deleteReportFile(report.getFilePath());
        
        nutritionReportRepository.delete(report);
    }
    
    
    private ReportSummaryDTO mapToReportSummaryDTO(NutritionReport report) {
        return new ReportSummaryDTO(
            report.getId(),
            report.getReportName(),
            report.getReportType(),
            report.getFileSize(),
            report.getGeneratedAt(),
            report.getStartDate(),
            report.getEndDate()
        );
    }
}
