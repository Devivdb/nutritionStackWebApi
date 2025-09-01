package com.nutritionstack.nutritionstackwebapi.controller.auth;

import com.nutritionstack.nutritionstackwebapi.dto.auth.AdminUserInfoDTO;
import com.nutritionstack.nutritionstackwebapi.dto.auth.AdminUserRoleUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.product.BulkUploadResponseDTO;
import com.nutritionstack.nutritionstackwebapi.service.auth.AdminService;
import com.nutritionstack.nutritionstackwebapi.service.product.BulkUploadService;
import com.nutritionstack.nutritionstackwebapi.util.GenericResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import com.nutritionstack.nutritionstackwebapi.exception.BulkUploadValidationException;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    private final BulkUploadService bulkUploadService;
    
    // User management endpoints
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserInfoDTO>> getAllUsers() {
        List<AdminUserInfoDTO> users = adminService.getAllUsers();
        return GenericResponseBuilder.success(users);
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserInfoDTO> getUserInfo(@PathVariable Long userId) {
        AdminUserInfoDTO userInfo = adminService.getUserInfo(userId);
        return GenericResponseBuilder.success(userInfo);
    }
    
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<AdminUserInfoDTO> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserRoleUpdateRequestDTO request) {
        AdminUserInfoDTO updatedUser = adminService.updateUserRole(userId, request);
        return GenericResponseBuilder.success(updatedUser);
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {
        String currentAdminUsername = authentication.getName();
        adminService.deleteUser(userId, currentAdminUsername);
        return GenericResponseBuilder.noContent();
    }
    
    // Bulk upload endpoints
    @PostMapping("/bulk-upload")
    public ResponseEntity<BulkUploadResponseDTO> createBulkUpload(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            Long userId = adminService.getUserIdByUsername(username);
            BulkUploadResponseDTO response = bulkUploadService.createBulkUpload(file, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BulkUploadValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BulkUploadValidationException("Failed to process bulk upload: " + e.getMessage());
        }
    }
    
    @GetMapping("/bulk-uploads")
    public ResponseEntity<List<BulkUploadResponseDTO>> getAllBulkUploads() {
        List<BulkUploadResponseDTO> uploads = bulkUploadService.getAllBulkUploads();
        return GenericResponseBuilder.success(uploads);
    }
    
    @GetMapping("/bulk-uploads/my")
    public ResponseEntity<List<BulkUploadResponseDTO>> getMyBulkUploads(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<BulkUploadResponseDTO> uploads = bulkUploadService.getBulkUploadsByUser(userId);
        return GenericResponseBuilder.success(uploads);
    }
    
    @GetMapping("/bulk-uploads/{bulkUploadId}")
    public ResponseEntity<BulkUploadResponseDTO> getBulkUploadById(@PathVariable Long bulkUploadId) {
        BulkUploadResponseDTO upload = bulkUploadService.getBulkUploadById(bulkUploadId);
        return GenericResponseBuilder.success(upload);
    }
    
    @DeleteMapping("/bulk-uploads/{bulkUploadId}/products")
    public ResponseEntity<Void> deleteProductsByBulkUploadId(@PathVariable Long bulkUploadId) {
        bulkUploadService.deleteProductsByBulkUploadId(bulkUploadId);
        return GenericResponseBuilder.noContent();
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        return adminService.getUserIdByUsername(username);
    }
}
