package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.service.NutritionCalculationService;
import com.nutritionstack.nutritionstackwebapi.exception.LoggedProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.exception.ProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.model.*;
import com.nutritionstack.nutritionstackwebapi.repository.LoggedProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggedProductServiceTest {
    
    @Mock
    private LoggedProductRepository loggedProductRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private NutritionCalculationService nutritionCalculationService;
    
    @InjectMocks
    private LoggedProductService loggedProductService;
    
    private Product testProduct;
    private LoggedProduct testLoggedProduct;
    private LoggedProductCreateRequestDTO createRequest;
    private LoggedProductUpdateRequestDTO updateRequest;
    
    @BeforeEach
    void setUp() {
        // Create test product
        testProduct = new Product();
        testProduct.setEan13Code("1234567890123");
        testProduct.setProductName("Test Product");
        testProduct.setAmount(100.0);
        testProduct.setUnit(Unit.G);
        
        NutritionInfo nutritionInfo = new NutritionInfo();
        nutritionInfo.setCalories(150.0);
        nutritionInfo.setProtein(10.0);
        nutritionInfo.setCarbs(20.0);
        nutritionInfo.setFat(5.0);
        nutritionInfo.setFiber(3.0);
        nutritionInfo.setSugar(8.0);
        nutritionInfo.setSalt(0.5);
        testProduct.setNutritionInfo(nutritionInfo);
        
        // Create test logged product
        testLoggedProduct = new LoggedProduct();
        testLoggedProduct.setId(1L);
        testLoggedProduct.setUserId(1L);
        testLoggedProduct.setEan13Code("1234567890123");
        testLoggedProduct.setQuantity(100.0);
        testLoggedProduct.setUnit(Unit.G);
        testLoggedProduct.setMealType(MealType.BREAKFAST);
        testLoggedProduct.setLogDate(LocalDateTime.now());
        testLoggedProduct.setCreatedAt(LocalDateTime.now());
        
        // Create test DTOs
        createRequest = new LoggedProductCreateRequestDTO();
        createRequest.setEan13Code("1234567890123");
        createRequest.setQuantity(100.0);
        createRequest.setUnit(Unit.G);
        createRequest.setMealType(MealType.BREAKFAST);
        
        updateRequest = new LoggedProductUpdateRequestDTO();
        updateRequest.setQuantity(150.0);
        updateRequest.setUnit(Unit.G);
    }
    
    @Test
    void logProduct_ValidRequest_ReturnsLoggedProductResponse() {
        // Arrange
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        when(loggedProductRepository.save(any(LoggedProduct.class)))
            .thenReturn(testLoggedProduct);
        
        NutritionCalculationService.CalculatedNutrition mockNutrition = 
            NutritionCalculationService.CalculatedNutrition.builder()
                .calories(150.0)
                .protein(10.0)
                .carbs(20.0)
                .fat(5.0)
                .fiber(3.0)
                .sugar(8.0)
                .salt(0.5)
                .build();
        
        when(nutritionCalculationService.calculateNutrition(
            any(), any(), any(), any(), any()))
            .thenReturn(mockNutrition);
        
        // Act
        LoggedProductResponseDTO result = loggedProductService.logProduct(createRequest, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals("1234567890123", result.getEan13Code());
        assertEquals(100.0, result.getQuantity());
        assertEquals("g", result.getUnit());
        assertEquals("breakfast", result.getMealType());
        assertEquals(150.0, result.getCalculatedCalories());
        assertEquals(10.0, result.getCalculatedProtein());
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(loggedProductRepository).save(any(LoggedProduct.class));
        verify(nutritionCalculationService).calculateNutrition(any(), any(), any(), any(), any());
    }
    
    @Test
    void logProduct_ProductNotFound_ThrowsException() {
        // Arrange
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            loggedProductService.logProduct(createRequest, 1L);
        });
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(loggedProductRepository, never()).save(any());
    }
    
    @Test
    void getUserLoggedProducts_ValidUserId_ReturnsList() {
        // Arrange
        List<LoggedProduct> loggedProducts = Arrays.asList(testLoggedProduct);
        when(loggedProductRepository.findByUserIdOrderByLogDateDesc(1L))
            .thenReturn(loggedProducts);
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        
        NutritionCalculationService.CalculatedNutrition mockNutrition = 
            NutritionCalculationService.CalculatedNutrition.builder()
                .calories(150.0)
                .protein(10.0)
                .carbs(20.0)
                .fat(5.0)
                .fiber(3.0)
                .sugar(8.0)
                .salt(0.5)
                .build();
        
        when(nutritionCalculationService.calculateNutrition(
            any(), any(), any(), any(), any()))
            .thenReturn(mockNutrition);
        
        // Act
        List<LoggedProductResponseDTO> result = loggedProductService.getUserLoggedProducts(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1234567890123", result.get(0).getEan13Code());
        
        verify(loggedProductRepository).findByUserIdOrderByLogDateDesc(1L);
        verify(nutritionCalculationService).calculateNutrition(any(), any(), any(), any(), any());
    }
    
    @Test
    void getLoggedProduct_ValidId_ReturnsLoggedProduct() {
        // Arrange
        when(loggedProductRepository.findByIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(testLoggedProduct));
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        
        NutritionCalculationService.CalculatedNutrition mockNutrition = 
            NutritionCalculationService.CalculatedNutrition.builder()
                .calories(150.0)
                .protein(10.0)
                .carbs(20.0)
                .fat(5.0)
                .fiber(3.0)
                .sugar(8.0)
                .salt(0.5)
                .build();
        
        when(nutritionCalculationService.calculateNutrition(
            any(), any(), any(), any(), any()))
            .thenReturn(mockNutrition);
        
        // Act
        LoggedProductResponseDTO result = loggedProductService.getLoggedProduct(1L, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("1234567890123", result.getEan13Code());
        
        verify(loggedProductRepository).findByIdAndUserId(1L, 1L);
        verify(nutritionCalculationService).calculateNutrition(any(), any(), any(), any(), any());
    }
    
    @Test
    void getLoggedProduct_NotFound_ThrowsException() {
        // Arrange
        when(loggedProductRepository.findByIdAndUserId(1L, 1L))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(LoggedProductNotFoundException.class, () -> {
            loggedProductService.getLoggedProduct(1L, 1L);
        });
        
        verify(loggedProductRepository).findByIdAndUserId(1L, 1L);
    }
    
    @Test
    void updateLoggedProduct_ValidRequest_ReturnsUpdatedProduct() {
        // Arrange
        when(loggedProductRepository.findByIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(testLoggedProduct));
        
        // Create updated logged product
        LoggedProduct updatedLoggedProduct = new LoggedProduct();
        updatedLoggedProduct.setId(1L);
        updatedLoggedProduct.setUserId(1L);
        updatedLoggedProduct.setEan13Code("1234567890123");
        updatedLoggedProduct.setQuantity(150.0); // Updated quantity
        updatedLoggedProduct.setUnit(Unit.G);
        updatedLoggedProduct.setMealType(MealType.BREAKFAST);
        updatedLoggedProduct.setLogDate(LocalDateTime.now());
        updatedLoggedProduct.setCreatedAt(LocalDateTime.now());
        
        when(loggedProductRepository.save(any(LoggedProduct.class)))
            .thenReturn(updatedLoggedProduct);
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        
        NutritionCalculationService.CalculatedNutrition mockNutrition = 
            NutritionCalculationService.CalculatedNutrition.builder()
                .calories(150.0)
                .protein(10.0)
                .carbs(20.0)
                .fat(5.0)
                .fiber(3.0)
                .sugar(8.0)
                .salt(0.5)
                .build();
        
        when(nutritionCalculationService.calculateNutrition(
            any(), any(), any(), any(), any()))
            .thenReturn(mockNutrition);
        
        // Act
        LoggedProductResponseDTO result = loggedProductService.updateLoggedProduct(1L, updateRequest, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(150.0, result.getQuantity()); // Updated value
        
        verify(loggedProductRepository).findByIdAndUserId(1L, 1L);
        verify(loggedProductRepository).save(any(LoggedProduct.class));
        verify(nutritionCalculationService).calculateNutrition(any(), any(), any(), any(), any());
    }
    
    @Test
    void deleteLoggedProduct_ValidId_DeletesSuccessfully() {
        // Arrange
        when(loggedProductRepository.existsByIdAndUserId(1L, 1L))
            .thenReturn(true);
        
        // Act
        loggedProductService.deleteLoggedProduct(1L, 1L);
        
        // Assert
        verify(loggedProductRepository).existsByIdAndUserId(1L, 1L);
        verify(loggedProductRepository).deleteById(1L);
    }
    
    @Test
    void deleteLoggedProduct_NotFound_ThrowsException() {
        // Arrange
        when(loggedProductRepository.existsByIdAndUserId(1L, 1L))
            .thenReturn(false);
        
        // Act & Assert
        assertThrows(LoggedProductNotFoundException.class, () -> {
            loggedProductService.deleteLoggedProduct(1L, 1L);
        });
        
        verify(loggedProductRepository).existsByIdAndUserId(1L, 1L);
        verify(loggedProductRepository, never()).deleteById(any());
    }
}
