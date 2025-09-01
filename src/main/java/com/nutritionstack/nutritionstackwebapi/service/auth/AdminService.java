package com.nutritionstack.nutritionstackwebapi.service.auth;

import com.nutritionstack.nutritionstackwebapi.dto.auth.AdminUserInfoDTO;
import com.nutritionstack.nutritionstackwebapi.dto.auth.AdminUserRoleUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.AdminCannotDeleteLastAdminException;
import com.nutritionstack.nutritionstackwebapi.exception.AdminCannotDeleteSelfException;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.auth.UserRole;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    
    public AdminUserInfoDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found with ID: %d", userId)));
        
        return mapToAdminUserInfoDTO(user);
    }
    
    public List<AdminUserInfoDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToAdminUserInfoDTO)
                .collect(Collectors.toList());
    }
    
    public AdminUserInfoDTO updateUserRole(Long userId, AdminUserRoleUpdateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found with ID: %d", userId)));
        
        user.setRole(request.getRole());
        User updatedUser = userRepository.save(user);
        
        return mapToAdminUserInfoDTO(updatedUser);
    }
    
    public void deleteUser(Long userId, String currentAdminUsername) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found with ID: %d", userId)));
        
        User currentAdmin = userRepository.findByUsername(currentAdminUsername)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Admin not found: %s", currentAdminUsername)));
        if (userToDelete.getId().equals(currentAdmin.getId())) {
            throw new AdminCannotDeleteSelfException("Admin cannot delete their own account");
        }
        if (userToDelete.getRole() == UserRole.ADMIN) {
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == UserRole.ADMIN)
                    .count();
            if (adminCount <= 1) {
                throw new AdminCannotDeleteLastAdminException("Cannot delete the last admin user");
            }
        }
        userRepository.delete(userToDelete);
    }
    
    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found with username: %s", username)));
        return user.getId();
    }
    
    private AdminUserInfoDTO mapToAdminUserInfoDTO(User user) {
        AdminUserInfoDTO dto = new AdminUserInfoDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
