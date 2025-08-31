package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.BulkUploadDataDTO;
import com.nutritionstack.nutritionstackwebapi.dto.BulkUploadResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.ProductCreateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.BulkUploadValidationException;
import com.nutritionstack.nutritionstackwebapi.model.BulkUpload;
import com.nutritionstack.nutritionstackwebapi.model.Unit;
import com.nutritionstack.nutritionstackwebapi.model.User;
import com.nutritionstack.nutritionstackwebapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulkUploadServiceTest {
    
    @Mock
    private BulkUploadFileService fileService;
    
    @Mock
    private BulkUploadValidationService validationService;
    
    @Mock
    private BulkUploadProcessingService processingService;
    
    @Mock
    private BulkUploadManagementService managementService;
    
    @Mock
    private ProductRepository productRepository;
    
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
        
        doNothing().when(fileService).validateUploadedFile(any(MockMultipartFile.class));
        when(fileService.parseJsonFile(any(MockMultipartFile.class))).thenReturn(validUploadData);
        when(fileService.getFileName(any(MockMultipartFile.class))).thenReturn("test_products.json");
        doNothing().when(validationService).validateBulkUploadData(any(BulkUploadDataDTO.class));
        when(productRepository.findEan13CodesByEan13CodeIn(anyList())).thenReturn(Arrays.asList());
        when(managementService.saveBulkUpload(any(BulkUpload.class))).thenReturn(savedBulkUpload);
        when(managementService.getUsernameById(userId)).thenReturn("testadmin");
        
        // Act
        var result = bulkUploadService.createBulkUpload(validJsonFile, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals("test_products.json", result.getFileName());
        assertEquals(1, result.getProductCount());
        assertEquals("testadmin", result.getUploadedByUsername());
        assertEquals("COMPLETED", result.getStatus());
        
        verify(fileService).validateUploadedFile(validJsonFile);
        verify(fileService).parseJsonFile(validJsonFile);
        verify(validationService).validateBulkUploadData(validUploadData);
        verify(processingService).processProducts(anyList(), eq(1L), eq(userId));
        verify(managementService, times(2)).saveBulkUpload(any(BulkUpload.class));
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
        
        doThrow(new BulkUploadValidationException("No file was uploaded"))
            .when(fileService).validateUploadedFile(emptyFile);
        
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
        
        doThrow(new BulkUploadValidationException("Only JSON files are allowed"))
            .when(fileService).validateUploadedFile(invalidFile);
        
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
        
        doThrow(new BulkUploadValidationException("File must have .json extension"))
            .when(fileService).validateUploadedFile(invalidFile);
        
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
        
        when(fileService.parseJsonFile(any(MockMultipartFile.class))).thenReturn(largeUploadData);
        doThrow(new BulkUploadValidationException("Cannot upload more than 1000 products at once"))
            .when(validationService).validateBulkUploadData(largeUploadData);
        
        // Act & Assert
        assertThrows(BulkUploadValidationException.class, () -> {
            bulkUploadService.createBulkUpload(validJsonFile, 1L);
        });
    }
    
    @Test
    void getAllBulkUploads_ShouldDelegateToManagementService() {
        // Arrange
        List<BulkUploadResponseDTO> expectedUploads = Arrays.asList();
        when(managementService.getAllBulkUploads()).thenReturn(expectedUploads);
        
        // Act
        var result = bulkUploadService.getAllBulkUploads();
        
        // Assert
        assertEquals(expectedUploads, result);
        verify(managementService).getAllBulkUploads();
    }
    
    @Test
    void getBulkUploadsByUser_ShouldDelegateToManagementService() {
        // Arrange
        Long userId = 1L;
        List<BulkUploadResponseDTO> expectedUploads = Arrays.asList();
        when(managementService.getBulkUploadsByUser(userId)).thenReturn(expectedUploads);
        
        // Act
        var result = bulkUploadService.getBulkUploadsByUser(userId);
        
        // Assert
        assertEquals(expectedUploads, result);
        verify(managementService).getBulkUploadsByUser(userId);
    }
    
    @Test
    void getBulkUploadById_ShouldDelegateToManagementService() {
        // Arrange
        Long bulkUploadId = 1L;
        BulkUploadResponseDTO expectedUpload = new BulkUploadResponseDTO();
        when(managementService.getBulkUploadById(bulkUploadId)).thenReturn(expectedUpload);
        
        // Act
        var result = bulkUploadService.getBulkUploadById(bulkUploadId);
        
        // Assert
        assertEquals(expectedUpload, result);
        verify(managementService).getBulkUploadById(bulkUploadId);
    }
    
    @Test
    void deleteProductsByBulkUploadId_ShouldDelegateToManagementService() {
        // Arrange
        Long bulkUploadId = 1L;
        
        // Act
        bulkUploadService.deleteProductsByBulkUploadId(bulkUploadId);
        
        // Assert
        verify(managementService).deleteProductsByBulkUploadId(bulkUploadId);
    }
}
