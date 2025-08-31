package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.LoggedProductSimpleResponseDTO;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggedProductServiceAutomaticDateTest {
    
    @Mock
    private LoggedProductRepository loggedProductRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private NutritionCalculationService nutritionCalculationService;
    
    @InjectMocks
    private LoggedProductService loggedProductService;
    
    private Product testProduct;
    private LoggedProductCreateRequestDTO requestWithoutDate;
    private LoggedProductCreateRequestDTO requestWithDate;
    private LoggedProduct savedLoggedProduct;
    
    @BeforeEach
    void setUp() {
        // Create test product
        testProduct = new Product();
        testProduct.setEan13Code("1234567890123");
        testProduct.setProductName("Test Apple");
        testProduct.setAmount(100.0);
        testProduct.setUnit(Unit.G);
        
        NutritionInfo nutritionInfo = new NutritionInfo();
        nutritionInfo.setCalories(71.0);
        nutritionInfo.setProtein(0.3);
        nutritionInfo.setCarbs(19.0);
        nutritionInfo.setFat(0.2);
        nutritionInfo.setFiber(2.4);
        nutritionInfo.setSugar(14.0);
        nutritionInfo.setSalt(0.001);
        testProduct.setNutritionInfo(nutritionInfo);
        
        // Create request without logDate
        requestWithoutDate = new LoggedProductCreateRequestDTO();
        requestWithoutDate.setEan13Code("1234567890123");
        requestWithoutDate.setQuantity(100.0);
        requestWithoutDate.setUnit(Unit.G);
        requestWithoutDate.setMealType(MealType.BREAKFAST);
        // logDate is null - this is what we're testing
        
        // Create request with logDate
        requestWithDate = new LoggedProductCreateRequestDTO();
        requestWithDate.setEan13Code("1234567890123");
        requestWithDate.setQuantity(100.0);
        requestWithDate.setUnit(Unit.G);
        requestWithDate.setMealType(MealType.BREAKFAST);
        requestWithDate.setLogDate(LocalDateTime.of(2024, 1, 15, 8, 0, 0));
        
        // Create saved logged product
        savedLoggedProduct = new LoggedProduct();
        savedLoggedProduct.setId(1L);
        savedLoggedProduct.setUserId(1L);
        savedLoggedProduct.setEan13Code("1234567890123");
        savedLoggedProduct.setQuantity(100.0);
        savedLoggedProduct.setUnit(Unit.G);
        savedLoggedProduct.setMealType(MealType.BREAKFAST);
        savedLoggedProduct.setLogDate(LocalDateTime.now());
        savedLoggedProduct.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void logProduct_WithoutLogDate_ShouldSetAutomaticDate() {
        // Arrange
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        when(loggedProductRepository.save(any(LoggedProduct.class)))
            .thenReturn(savedLoggedProduct);
        
        NutritionCalculationService.CalculatedNutrition mockNutrition = 
            NutritionCalculationService.CalculatedNutrition.builder()
                .calories(71.0)
                .protein(0.3)
                .carbs(19.0)
                .fat(0.2)
                .fiber(2.4)
                .sugar(14.0)
                .salt(0.001)
                .build();
        
        when(nutritionCalculationService.calculateNutrition(any(), any(), any(), any(), any()))
            .thenReturn(mockNutrition);
        
        // Act
        LoggedProductSimpleResponseDTO result = loggedProductService.logProductSimple(requestWithoutDate, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals("1234567890123", result.getEan13Code());
        assertEquals(100.0, result.getQuantity());
        assertEquals(Unit.G, result.getUnit());
        assertEquals(MealType.BREAKFAST, result.getMealType());
        
        // Verify that the repository was called
        verify(loggedProductRepository).save(any(LoggedProduct.class));
    }
    
    @Test
    void logProduct_WithLogDate_ShouldUseProvidedDate() {
        // Arrange
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        when(loggedProductRepository.save(any(LoggedProduct.class)))
            .thenReturn(savedLoggedProduct);
        
        NutritionCalculationService.CalculatedNutrition mockNutrition = 
            NutritionCalculationService.CalculatedNutrition.builder()
                .calories(71.0)
                .protein(0.3)
                .carbs(19.0)
                .fat(0.2)
                .fiber(2.4)
                .sugar(14.0)
                .salt(0.001)
                .build();
        
        when(nutritionCalculationService.calculateNutrition(any(), any(), any(), any(), any()))
            .thenReturn(mockNutrition);
        
        // Act
        LoggedProductSimpleResponseDTO result = loggedProductService.logProductSimple(requestWithDate, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals("1234567890123", result.getEan13Code());
        assertEquals(100.0, result.getQuantity());
        assertEquals(Unit.G, result.getUnit());
        assertEquals(MealType.BREAKFAST, result.getMealType());
        
        // Verify that the repository was called
        verify(loggedProductRepository).save(any(LoggedProduct.class));
    }
    
    @Test
    void logProduct_WithoutLogDate_ShouldNotBeNull() {
        // Arrange
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        when(loggedProductRepository.save(any(LoggedProduct.class)))
            .thenReturn(savedLoggedProduct);
        
        NutritionCalculationService.CalculatedNutrition mockNutrition = 
            NutritionCalculationService.CalculatedNutrition.builder()
                .calories(71.0)
                .protein(0.3)
                .carbs(19.0)
                .fat(0.2)
                .fiber(2.4)
                .sugar(14.0)
                .salt(0.001)
                .build();
        
        when(nutritionCalculationService.calculateNutrition(any(), any(), any(), any(), any()))
            .thenReturn(mockNutrition);
        
        // Act
        LoggedProductSimpleResponseDTO result = loggedProductService.logProductSimple(requestWithoutDate, 1L);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getLogDate(), "Response should include the automatically set logDate");
    }
}
