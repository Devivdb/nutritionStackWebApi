package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.product.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.product.ProductResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.product.ProductUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.ProductAlreadyExistsException;
import com.nutritionstack.nutritionstackwebapi.exception.ProductNotFoundException;
import com.nutritionstack.nutritionstackwebapi.exception.ProductValidationException;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.NutritionInfo;
import com.nutritionstack.nutritionstackwebapi.model.product.Product;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.Unit;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
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
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private NutritionService nutritionService;
    
    @InjectMocks
    private ProductService productService;
    
    private User testUser;
    private Product testProduct;
    private ProductCreateRequestDTO createRequest;
    private ProductUpdateRequestDTO updateRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password", com.nutritionstack.nutritionstackwebapi.model.auth.UserRole.USER);
        testUser.setId(1L);
        
        NutritionInfo nutritionInfo = new NutritionInfo();
        nutritionInfo.setCalories(100.0);
        nutritionInfo.setProtein(5.0);
        nutritionInfo.setCarbs(20.0);
        nutritionInfo.setFat(2.0);
        nutritionInfo.setFiber(3.0);
        nutritionInfo.setSugar(10.0);
        nutritionInfo.setSalt(0.5);
        
        testProduct = new Product();
        testProduct.setEan13Code("1234567890123");
        testProduct.setProductName("Test Product");
        testProduct.setNutritionInfo(nutritionInfo);
        testProduct.setAmount(100.0);
        testProduct.setUnit(Unit.G);
        testProduct.setCreatedBy(1L);
        testProduct.setCreatedAt(LocalDateTime.now());
        
        createRequest = new ProductCreateRequestDTO();
        createRequest.setEan13Code("1234567890123");
        createRequest.setProductName("Test Product");
        createRequest.setAmount(100.0);
        createRequest.setUnit(Unit.G);
        createRequest.setCalories(100.0);
        createRequest.setProtein(5.0);
        createRequest.setCarbs(20.0);
        createRequest.setFat(2.0);
        createRequest.setFiber(3.0);
        createRequest.setSugar(10.0);
        createRequest.setSalt(0.5);
        
        updateRequest = new ProductUpdateRequestDTO();
        updateRequest.setProductName("Updated Product");
        updateRequest.setAmount(150.0);
        updateRequest.setUnit(Unit.ML);
        updateRequest.setCalories(150.0);
    }
    
    @Test
    void createProduct_ShouldCreateProductSuccessfully() {
        when(productRepository.existsByEan13Code("1234567890123")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        NutritionInfo mockNutritionInfo = new NutritionInfo();
        mockNutritionInfo.setCalories(100.0);
        mockNutritionInfo.setProtein(5.0);
        mockNutritionInfo.setCarbs(20.0);
        mockNutritionInfo.setFat(2.0);
        mockNutritionInfo.setFiber(3.0);
        mockNutritionInfo.setSugar(10.0);
        mockNutritionInfo.setSalt(0.5);
        when(nutritionService.createNutritionInfoWithDefaults(createRequest)).thenReturn(mockNutritionInfo);
        
        ProductResponseDTO result = productService.createProduct(createRequest, 1L);
        
        assertNotNull(result);
        assertEquals("1234567890123", result.getEan13Code());
        assertEquals("Test Product", result.getProductName());
        assertEquals(100.0, result.getAmount());
        assertEquals(Unit.G, result.getUnit());
        assertEquals("testuser", result.getCreatedByUsername());
        
        verify(productRepository).existsByEan13Code("1234567890123");
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void createProduct_ShouldThrowException_WhenProductAlreadyExists() {
        when(productRepository.existsByEan13Code("1234567890123")).thenReturn(true);
        
        assertThrows(ProductAlreadyExistsException.class, () -> {
            productService.createProduct(createRequest, 1L);
        });
        
        verify(productRepository).existsByEan13Code("1234567890123");
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void getProductByEan13Code_ShouldReturnProduct_WhenProductExists() {
        when(productRepository.findByEan13Code("1234567890123")).thenReturn(Optional.of(testProduct));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        ProductResponseDTO result = productService.getProductByEan13Code("1234567890123");
        
        assertNotNull(result);
        assertEquals("1234567890123", result.getEan13Code());
        assertEquals("Test Product", result.getProductName());
        assertEquals(100.0, result.getAmount());
        assertEquals(Unit.G, result.getUnit());
        assertEquals("testuser", result.getCreatedByUsername());
        
        verify(productRepository).findByEan13Code("1234567890123");
    }
    
    @Test
    void getProductByEan13Code_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findByEan13Code("1234567890123")).thenReturn(Optional.empty());
        
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductByEan13Code("1234567890123");
        });
        
        verify(productRepository).findByEan13Code("1234567890123");
    }
    
    @Test
    void updateProduct_ShouldUpdateProductSuccessfully() {
        when(productRepository.findByEan13Code("1234567890123")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        doNothing().when(nutritionService).updateNutritionInfo(any(NutritionInfo.class), eq(updateRequest));
        
        ProductResponseDTO result = productService.updateProduct("1234567890123", updateRequest, 1L);
        
        assertNotNull(result);
        assertEquals("Updated Product", result.getProductName());
        assertEquals(150.0, result.getAmount());
        assertEquals(Unit.ML, result.getUnit());
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findByEan13Code("1234567890123")).thenReturn(Optional.empty());
        
        assertThrows(ProductValidationException.class, () -> {
            productService.updateProduct("1234567890123", updateRequest, 1L);
        });
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void deleteProduct_ShouldDeleteProductSuccessfully() {
        when(productRepository.findByEan13Code("1234567890123")).thenReturn(Optional.of(testProduct));
        
        productService.deleteProduct("1234567890123");
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(productRepository).delete(testProduct);
    }
    
    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findByEan13Code("1234567890123")).thenReturn(Optional.empty());
        
        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct("1234567890123");
        });
        
        verify(productRepository).findByEan13Code("1234567890123");
        verify(productRepository, never()).delete(any(Product.class));
    }
    
    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        List<Product> products = List.of(testProduct);
        when(productRepository.findAll()).thenReturn(products);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        List<ProductResponseDTO> result = productService.getAllProducts();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1234567890123", result.get(0).getEan13Code());
        
        verify(productRepository).findAll();
    }
    
    @Test
    void convertToResponseDTO_ShouldReturnUnknownUser_WhenUserNotFound() {
        when(productRepository.findByEan13Code("1234567890123")).thenReturn(Optional.of(testProduct));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        ProductResponseDTO result = productService.getProductByEan13Code("1234567890123");
        
        assertEquals("Unknown User", result.getCreatedByUsername());
    }
}
