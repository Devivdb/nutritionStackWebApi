package com.nutritionstack.nutritionstackwebapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.nutritionstack.nutritionstackwebapi.dto.BulkUploadDataDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.BulkUploadValidationException;
import com.nutritionstack.nutritionstackwebapi.model.BulkUpload;
import com.nutritionstack.nutritionstackwebapi.model.Unit;
import com.nutritionstack.nutritionstackwebapi.model.User;
import com.nutritionstack.nutritionstackwebapi.repository.BulkUploadRepository;
import com.nutritionstack.nutritionstackwebapi.repository.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulkUploadServiceTest {
    
    @Mock
    private BulkUploadRepository bulkUploadRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private BulkUploadService bulkUploadService;
    
    private User testUser;
    private ProductCreateRequestDTO validProduct;
    private BulkUploadDataDTO validUploadData;
    private MockMultipartFile validJsonFile;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testadmin");
        
        validProduct = new ProductCreateRequestDTO();
        validProduct.setEan13Code("1234567890123");
        validProduct.setProductName("Test Product");
        validProduct.setAmount(100.0);
        validProduct.setUnit(Unit.G);
        validProduct.setCalories(150.0);
        validProduct.setProtein(10.0);
        validProduct.setCarbs(20.0);
        validProduct.setFat(5.0);
        
        validUploadData = new BulkUploadDataDTO();
        validUploadData.setProducts(Arrays.asList(validProduct));
        
        validJsonFile = new MockMultipartFile(
            "file",
            "test_products.json",
            "application/json",
            "{\"products\":[]}".getBytes()
        );
    }
    
    @Test
    void createBulkUpload_WithValidFile_ShouldSucceed() throws Exception {
        // Arrange
        Long userId = 1L;
        
        BulkUpload savedBulkUpload = new BulkUpload("test_products.json", 1, userId);
        savedBulkUpload.setId(1L);
        savedBulkUpload.setStatus(BulkUpload.BulkUploadStatus.COMPLETED);
        
        when(bulkUploadRepository.save(any(BulkUpload.class))).thenReturn(savedBulkUpload);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(objectMapper.readValue(any(java.io.InputStream.class), eq(BulkUploadDataDTO.class))).thenReturn(validUploadData);
        when(productRepository.findEan13CodesByEan13CodeIn(anyList())).thenReturn(Arrays.asList());
        when(productRepository.save(any())).thenReturn(null);
        
        // Act
        var result = bulkUploadService.createBulkUpload(validJsonFile, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals("test_products.json", result.getFileName());
        assertEquals(1, result.getProductCount());
        assertEquals("testadmin", result.getUploadedByUsername());
        assertEquals("COMPLETED", result.getStatus());
        
        verify(bulkUploadRepository, times(2)).save(any(BulkUpload.class));
        verify(productRepository).save(any());
    }
    
    @Test
    void createBulkUpload_WithEmptyFile_ShouldThrowException() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.json",
            "application/json",
            new byte[0]
        );
        
        // Act & Assert
        assertThrows(BulkUploadValidationException.class, () -> {
            bulkUploadService.createBulkUpload(emptyFile, 1L);
        });
    }
    
    @Test
    void createBulkUpload_WithInvalidContentType_ShouldThrowException() {
        // Arrange
        MockMultipartFile invalidFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "some content".getBytes()
        );
        
        // Act & Assert
        assertThrows(BulkUploadValidationException.class, () -> {
            bulkUploadService.createBulkUpload(invalidFile, 1L);
        });
    }
    
    @Test
    void createBulkUpload_WithInvalidFileExtension_ShouldThrowException() {
        // Arrange
        MockMultipartFile invalidFile = new MockMultipartFile(
            "file",
            "test.txt",
            "application/json",
            "{\"products\":[]}".getBytes()
        );
        
        // Act & Assert
        assertThrows(BulkUploadValidationException.class, () -> {
            bulkUploadService.createBulkUpload(invalidFile, 1L);
        });
    }
    
    @Test
    void createBulkUpload_WithTooManyProducts_ShouldThrowException() throws Exception {
        // Arrange
        BulkUploadDataDTO largeUploadData = new BulkUploadDataDTO();
        List<ProductCreateRequestDTO> manyProducts = Arrays.asList(new ProductCreateRequestDTO[1001]);
        largeUploadData.setProducts(manyProducts);
        
        when(objectMapper.readValue(any(java.io.InputStream.class), eq(BulkUploadDataDTO.class))).thenReturn(largeUploadData);
        
        // Act & Assert
        assertThrows(BulkUploadValidationException.class, () -> {
            bulkUploadService.createBulkUpload(validJsonFile, 1L);
        });
    }
}
