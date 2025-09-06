package com.nutritionstack.nutritionstackwebapi.service.nutrition;

import com.nutritionstack.nutritionstackwebapi.dto.nutrition.ReportGenerationRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.nutrition.ReportGenerationResponseDTO;
import com.nutritionstack.nutritionstackwebapi.exception.ResourceNotFoundException;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.NutritionReport;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.UserGoal;
import com.nutritionstack.nutritionstackwebapi.model.tracking.LoggedProduct;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import com.nutritionstack.nutritionstackwebapi.repository.nutrition.NutritionReportRepository;
import com.nutritionstack.nutritionstackwebapi.repository.tracking.LoggedProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NutritionReportServiceTest {

    @Mock
    private NutritionReportRepository nutritionReportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoggedProductRepository loggedProductRepository;

    @Mock
    private UserGoalService userGoalService;

    @Mock
    private NutritionAggregationService nutritionAggregationService;

    @Mock
    private PdfReportGeneratorService pdfReportGeneratorService;

    @InjectMocks
    private NutritionReportService nutritionReportService;

    private User testUser;
    private UserGoal testUserGoal;
    private LoggedProduct testLoggedProduct;
    private ReportGenerationRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testUserGoal = new UserGoal();
        testUserGoal.setId(1L);
        testUserGoal.setCaloriesGoal(2000.0);
        testUserGoal.setProteinGoal(150.0);
        testUserGoal.setCarbsGoal(200.0);
        testUserGoal.setFatGoal(67.0);

        testLoggedProduct = new LoggedProduct();
        testLoggedProduct.setId(1L);
        testLoggedProduct.setUserId(1L);
        testLoggedProduct.setEan13Code("1234567890123");
        testLoggedProduct.setQuantity(100.0);

        testRequest = new ReportGenerationRequestDTO();
        testRequest.setReportType("DAY");
        testRequest.setStartDate(LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0));
        testRequest.setEndDate(LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59));
    }

    @Test
    void generateReport_WithValidDayRequest_ShouldSucceed() throws Exception {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userGoalService.getActiveGoalEntity(1L)).thenReturn(testUserGoal);
        when(loggedProductRepository.findByUserIdAndLogDateBetweenOrderByLogDateDesc(
            anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testLoggedProduct));

        NutritionAggregationService.NutritionSummary mockSummary = 
            new NutritionAggregationService.NutritionSummary();
        when(nutritionAggregationService.calculateNutritionSummary(
            anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(mockSummary);

        NutritionAggregationService.ProgressSummary mockProgress = 
            new NutritionAggregationService.ProgressSummary();
        when(nutritionAggregationService.calculateProgressSummary(any(), any()))
            .thenReturn(mockProgress);

        byte[] mockPdfContent = "Mock PDF content".getBytes();
        when(pdfReportGeneratorService.generateNutritionReport(
            any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(mockPdfContent);

        NutritionReport savedReport = new NutritionReport();
        savedReport.setId(1L);
        savedReport.setReportName("DAY Report (Sep 03 - Sep 03, 2025)");
        savedReport.setReportType("DAY");
        savedReport.setFilePath("/reports/test.txt");
        savedReport.setFileSize((long) mockPdfContent.length);
        savedReport.setGeneratedAt(LocalDateTime.now());
        when(nutritionReportRepository.save(any(NutritionReport.class))).thenReturn(savedReport);

        // Act
        ReportGenerationResponseDTO result = nutritionReportService.generateReport(1L, testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getReportId());
        assertEquals("DAY Report (Sep 03 - Sep 03, 2025)", result.getReportName());
        assertEquals("DAY", result.getReportType());
        assertEquals("Report generated successfully", result.getMessage());
        assertTrue(result.getDownloadUrl().contains("/api/nutrition-reports/1/download"));
        assertEquals((long) mockPdfContent.length, result.getFileSize());

        verify(nutritionReportRepository).save(any(NutritionReport.class));
        verify(pdfReportGeneratorService).generateNutritionReport(
            any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void generateReport_WithInvalidRequest_ShouldThrowException() {
        // Arrange
        ReportGenerationRequestDTO invalidRequest = new ReportGenerationRequestDTO();
        invalidRequest.setReportType("INVALID");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            nutritionReportService.generateReport(1L, invalidRequest);
        });
    }

    @Test
    void generateReport_WithNoLoggedProducts_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(loggedProductRepository.findByUserIdAndLogDateBetweenOrderByLogDateDesc(
            anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            nutritionReportService.generateReport(1L, testRequest);
        });
    }

    @Test
    void generateReport_WithUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            nutritionReportService.generateReport(1L, testRequest);
        });
    }

    @Test
    void generateReport_WithoutUserGoal_ShouldSucceed() throws Exception {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userGoalService.getActiveGoalEntity(1L)).thenThrow(new RuntimeException("No goal"));
        when(loggedProductRepository.findByUserIdAndLogDateBetweenOrderByLogDateDesc(
            anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(testLoggedProduct));

        NutritionAggregationService.NutritionSummary mockSummary = 
            new NutritionAggregationService.NutritionSummary();
        when(nutritionAggregationService.calculateNutritionSummary(
            anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(mockSummary);

        NutritionAggregationService.ProgressSummary mockProgress = 
            new NutritionAggregationService.ProgressSummary();
        when(nutritionAggregationService.calculateProgressSummary(any(), any()))
            .thenReturn(mockProgress);

        byte[] mockPdfContent = "Mock PDF content".getBytes();
        when(pdfReportGeneratorService.generateNutritionReport(
            any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(mockPdfContent);

        NutritionReport savedReport = new NutritionReport();
        savedReport.setId(1L);
        savedReport.setReportName("DAY Report (Sep 03 - Sep 03, 2025)");
        savedReport.setReportType("DAY");
        savedReport.setFilePath("/reports/test.txt");
        savedReport.setFileSize((long) mockPdfContent.length);
        savedReport.setGeneratedAt(LocalDateTime.now());
        when(nutritionReportRepository.save(any(NutritionReport.class))).thenReturn(savedReport);

        // Act
        ReportGenerationResponseDTO result = nutritionReportService.generateReport(1L, testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getReportId());
    }

    @Test
    void getUserReports_ShouldReturnUserReports() {
        // Arrange
        NutritionReport mockReport = new NutritionReport();
        mockReport.setId(1L);
        mockReport.setReportName("Test Report");
        mockReport.setReportType("DAY");
        mockReport.setFileSize(1024L);
        mockReport.setGeneratedAt(LocalDateTime.now());
        mockReport.setStartDate(LocalDateTime.now());
        mockReport.setEndDate(LocalDateTime.now());

        when(nutritionReportRepository.findByUserIdOrderByGeneratedAtDesc(1L))
            .thenReturn(Arrays.asList(mockReport));

        // Act
        var result = nutritionReportService.getUserReports(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getReportId());
        assertEquals("Test Report", result.get(0).getReportName());
    }

    @Test
    void getReportById_WithValidId_ShouldReturnReport() {
        // Arrange
        NutritionReport mockReport = new NutritionReport();
        mockReport.setId(1L);
        mockReport.setReportName("Test Report");
        mockReport.setReportType("DAY");
        mockReport.setFileSize(1024L);
        mockReport.setGeneratedAt(LocalDateTime.now());
        mockReport.setStartDate(LocalDateTime.now());
        mockReport.setEndDate(LocalDateTime.now());

        when(nutritionReportRepository.findByIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(mockReport));

        // Act
        var result = nutritionReportService.getReportById(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getReportId());
        assertEquals("Test Report", result.getReportName());
    }

    @Test
    void getReportById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(nutritionReportRepository.findByIdAndUserId(999L, 1L))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            nutritionReportService.getReportById(999L, 1L);
        });
    }
}
