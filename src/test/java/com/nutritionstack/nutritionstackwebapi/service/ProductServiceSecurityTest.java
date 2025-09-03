package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.product.ProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.UnauthorizedAccessException;
import com.nutritionstack.nutritionstackwebapi.model.auth.*;
import com.nutritionstack.nutritionstackwebapi.model.product.*;
import com.nutritionstack.nutritionstackwebapi.model.meal.*;
import com.nutritionstack.nutritionstackwebapi.model.tracking.*;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.*;
import com.nutritionstack.nutritionstackwebapi.repository.product.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import com.nutritionstack.nutritionstackwebapi.service.nutrition.NutritionService;
import com.nutritionstack.nutritionstackwebapi.service.product.ProductService;
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
class ProductServiceSecurityTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private NutritionService nutritionService;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    private User testUser;
    private User otherUser;
    private User adminUser;
    private ProductUpdateRequestDTO updateRequest;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setEan13Code("1234567890123");
        testProduct.setProductName("Test Product");
        testProduct.setAmount(100.0);
        testProduct.setUnit(Unit.G);
        testProduct.setCreatedBy(1L);
        testProduct.setCreatedAt(LocalDateTime.now());
        
        NutritionInfo nutritionInfo = new NutritionInfo();
        nutritionInfo.setCalories(150.0);
        nutritionInfo.setProtein(10.0);
        nutritionInfo.setCarbs(20.0);
        nutritionInfo.setFat(5.0);
        nutritionInfo.setFiber(3.0);
        nutritionInfo.setSugar(8.0);
        nutritionInfo.setSalt(0.5);
        testProduct.setNutritionInfo(nutritionInfo);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(UserRole.USER);
        
        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");
        otherUser.setRole(UserRole.USER);
        
        adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("adminuser");
        adminUser.setRole(UserRole.ADMIN);
        
        updateRequest = new ProductUpdateRequestDTO();
        updateRequest.setProductName("Updated Product Name");
        updateRequest.setAmount(150.0);
        updateRequest.setUnit(Unit.ML);
        updateRequest.setCalories(160.0);
    }
    
    @Test
    void updateProduct_UserOwnsProduct_ShouldSucceed() {
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class)))
            .thenReturn(testProduct);
        
        assertDoesNotThrow(() -> {
            productService.updateProduct("1234567890123", updateRequest, 1L);
        });
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void updateProduct_UserDoesNotOwnProduct_ShouldThrowUnauthorizedAccessException() {
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        when(userRepository.findById(2L))
            .thenReturn(Optional.of(otherUser));
        
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            productService.updateProduct("1234567890123", updateRequest, 2L);
        });
        
        assertTrue(exception.getMessage().contains("Access denied"));
        assertTrue(exception.getMessage().contains("Test Product"));
        assertTrue(exception.getMessage().contains("was created by another user"));
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(userRepository).findById(2L);
        verify(productRepository, never()).save(any());
    }
    
    @Test
    void updateProduct_AdminUser_ShouldSucceed() {
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        when(userRepository.findById(3L))
            .thenReturn(Optional.of(adminUser));
        when(productRepository.save(any(Product.class)))
            .thenReturn(testProduct);
        
        assertDoesNotThrow(() -> {
            productService.updateProduct("1234567890123", updateRequest, 3L);
        });
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(userRepository).findById(3L);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void updateProduct_UserNotFound_ShouldThrowRuntimeException() {
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        when(userRepository.findById(999L))
            .thenReturn(Optional.empty());
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct("1234567890123", updateRequest, 999L);
        });
        
        assertTrue(exception.getMessage().contains("User not found with ID: 999"));
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(userRepository).findById(999L);
        verify(productRepository, never()).save(any());
    }
    
    @Test
    void deleteProductWithOwnershipCheck_UserOwnsProduct_ShouldSucceed() {
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        
        assertDoesNotThrow(() -> {
            productService.deleteProductWithOwnershipCheck("1234567890123", 1L);
        });
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(productRepository).delete(testProduct);
    }
    
    @Test
    void deleteProductWithOwnershipCheck_UserDoesNotOwnProduct_ShouldThrowUnauthorizedAccessException() {
        when(productRepository.findByEan13Code("1234567890123"))
            .thenReturn(Optional.of(testProduct));
        when(userRepository.findById(2L))
            .thenReturn(Optional.of(otherUser));
        
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            productService.deleteProductWithOwnershipCheck("1234567890123", 2L);
        });
        
        assertTrue(exception.getMessage().contains("Access denied"));
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(userRepository).findById(2L);
        verify(productRepository, never()).delete(any());
    }
}
