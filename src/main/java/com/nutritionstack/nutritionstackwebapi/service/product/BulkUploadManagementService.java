package com.nutritionstack.nutritionstackwebapi.service.product;

import com.nutritionstack.nutritionstackwebapi.dto.product.BulkUploadResponseDTO;
import com.nutritionstack.nutritionstackwebapi.model.product.BulkUpload;
import com.nutritionstack.nutritionstackwebapi.repository.product.BulkUploadRepository;
import com.nutritionstack.nutritionstackwebapi.repository.product.ProductRepository;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BulkUploadManagementService {
    
    private final BulkUploadRepository bulkUploadRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    public BulkUploadManagementService(BulkUploadRepository bulkUploadRepository,
                                     ProductRepository productRepository,
                                     UserRepository userRepository) {
        this.bulkUploadRepository = bulkUploadRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    
    public BulkUpload saveBulkUpload(BulkUpload bulkUpload) {
        return bulkUploadRepository.save(bulkUpload);
    }
    
    public List<BulkUploadResponseDTO> getAllBulkUploads() {
        return bulkUploadRepository.findAllByOrderByUploadedAtDesc().stream()
                .map(upload -> {
                    String username = getUsernameById(upload.getUploadedBy());
                    return BulkUploadResponseDTO.fromEntity(upload, username);
                })
                .collect(Collectors.toList());
    }
    
    public List<BulkUploadResponseDTO> getBulkUploadsByUser(Long userId) {
        return bulkUploadRepository.findByUploadedByOrderByUploadedAtDesc(userId).stream()
                .map(upload -> {
                    String username = getUsernameById(userId);
                    return BulkUploadResponseDTO.fromEntity(upload, username);
                })
                .collect(Collectors.toList());
    }
    
    public BulkUploadResponseDTO getBulkUploadById(Long bulkUploadId) {
        BulkUpload bulkUpload = bulkUploadRepository.findById(bulkUploadId)
                .orElseThrow(() -> new IllegalArgumentException("Bulk upload not found with ID: " + bulkUploadId));
        
        String username = getUsernameById(bulkUpload.getUploadedBy());
        return BulkUploadResponseDTO.fromEntity(bulkUpload, username);
    }
    
    @Transactional
    public void deleteProductsByBulkUploadId(Long bulkUploadId) {
        if (!bulkUploadRepository.existsById(bulkUploadId)) {
            throw new IllegalArgumentException("Bulk upload not found with ID: " + bulkUploadId);
        }
        productRepository.deleteByBulkUploadId(bulkUploadId);
    }
    
    public String getUsernameById(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("Unknown User");
    }
}
