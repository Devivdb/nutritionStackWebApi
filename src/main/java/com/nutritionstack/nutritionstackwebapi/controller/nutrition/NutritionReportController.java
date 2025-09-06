package com.nutritionstack.nutritionstackwebapi.controller.nutrition;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.ReportGenerationRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.ReportGenerationResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.ReportSummaryDTO;
import com.nutritionstack.nutritionstackwebapi.service.nutrition.NutritionReportService;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.nutritionstack.nutritionstackwebapi.security.CustomAuthenticationToken;

import java.util.List;

@RestController
@RequestMapping("/api/nutrition-reports")
@CrossOrigin(origins = "*")
public class NutritionReportController {
    
    private final NutritionReportService nutritionReportService;
    
    public NutritionReportController(NutritionReportService nutritionReportService) {
        this.nutritionReportService = nutritionReportService;
    }
    
    @PostMapping
    public ResponseEntity<byte[]> generateReport(
            @Valid @RequestBody ReportGenerationRequestDTO request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        byte[] reportContent = nutritionReportService.generateReportPdf(userId, request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "nutrition_report.pdf");
        headers.setContentLength(reportContent.length);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(headers)
            .body(reportContent);
    }
    
    @GetMapping
    public ResponseEntity<List<ReportSummaryDTO>> getUserReports(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ReportSummaryDTO> reports = nutritionReportService.getUserReports(userId);
        
        return ResponseEntity.ok(reports);
    }
    
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportSummaryDTO> getReportById(
            @PathVariable Long reportId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        ReportSummaryDTO report = nutritionReportService.getReportById(reportId, userId);
        
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/{reportId}/download")
    public ResponseEntity<byte[]> downloadReport(
            @PathVariable Long reportId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        byte[] reportContent = nutritionReportService.downloadReport(reportId, userId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "nutrition_report.pdf");
        headers.setContentLength(reportContent.length);
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(reportContent);
    }
    
    @DeleteMapping("/{reportId}")
    public ResponseEntity<String> deleteReport(
            @PathVariable Long reportId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        nutritionReportService.deleteReport(reportId, userId);
        
        return ResponseEntity.ok("Report deleted successfully");
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication required");
        }

        if (authentication instanceof CustomAuthenticationToken) {
            CustomAuthenticationToken customToken = (CustomAuthenticationToken) authentication;
            return customToken.getUserId();
        }

        if (authentication.getPrincipal() != null) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid user ID in authentication");
            }
        }
        
        throw new IllegalArgumentException("Unable to extract user ID from authentication");
    }
}
