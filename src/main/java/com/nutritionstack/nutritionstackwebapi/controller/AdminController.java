package com.nutritionstack.nutritionstackwebapi.controller;

import com.nutritionstack.nutritionstackwebapi.dto.AdminUserInfoDTO;
import com.nutritionstack.nutritionstackwebapi.dto.AdminUserRoleUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.service.AdminService;
import com.nutritionstack.nutritionstackwebapi.util.GenericResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping
    public ResponseEntity<List<AdminUserInfoDTO>> getAllUsers() {
        List<AdminUserInfoDTO> users = adminService.getAllUsers();
        return GenericResponseBuilder.success(users);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserInfoDTO> getUserInfo(@PathVariable Long userId) {
        AdminUserInfoDTO userInfo = adminService.getUserInfo(userId);
        return GenericResponseBuilder.success(userInfo);
    }
    
    @PatchMapping("/{userId}/role")
    public ResponseEntity<AdminUserInfoDTO> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserRoleUpdateRequestDTO request) {
        AdminUserInfoDTO updatedUser = adminService.updateUserRole(userId, request);
        return GenericResponseBuilder.success(updatedUser);
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {
        String currentAdminUsername = authentication.getName();
        adminService.deleteUser(userId, currentAdminUsername);
        return GenericResponseBuilder.noContent();
    }
}
