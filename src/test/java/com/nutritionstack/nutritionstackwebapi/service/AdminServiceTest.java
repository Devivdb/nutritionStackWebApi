package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.AdminUserInfoDTO;
import com.nutritionstack.nutritionstackwebapi.dto.AdminUserRoleUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.AdminCannotDeleteLastAdminException;
import com.nutritionstack.nutritionstackwebapi.exception.AdminCannotDeleteSelfException;
import com.nutritionstack.nutritionstackwebapi.model.User;
import com.nutritionstack.nutritionstackwebapi.model.UserRole;
import com.nutritionstack.nutritionstackwebapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private User testUser;
    private User testAdmin;
    private AdminUserRoleUpdateRequestDTO roleUpdateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .password("password")
                .role(UserRole.USER)
                .build();
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());

        testAdmin = User.builder()
                .username("admin")
                .password("password")
                .role(UserRole.ADMIN)
                .build();
        testAdmin.setId(2L);
        testAdmin.setCreatedAt(LocalDateTime.now());

        roleUpdateRequest = new AdminUserRoleUpdateRequestDTO();
        roleUpdateRequest.setRole(UserRole.ADMIN);
    }

    @Test
    void should_GetUserInfo_When_UserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        AdminUserInfoDTO result = adminService.getUserInfo(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getRole(), result.getRole());
        assertEquals(testUser.getCreatedAt(), result.getCreatedAt());
        verify(userRepository).findById(1L);
    }

    @Test
    void should_ThrowException_When_GetUserInfo_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> adminService.getUserInfo(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void should_GetAllUsers_When_UsersExist() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testAdmin);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<AdminUserInfoDTO> result = adminService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(testAdmin.getId(), result.get(1).getId());
        verify(userRepository).findAll();
    }

    @Test
    void should_UpdateUserRole_When_UserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        AdminUserInfoDTO result = adminService.updateUserRole(1L, roleUpdateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(UserRole.ADMIN, testUser.getRole());
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void should_ThrowException_When_UpdateUserRole_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> adminService.updateUserRole(999L, roleUpdateRequest));
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void should_DeleteUser_When_ValidUserAndNotSelf() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testAdmin));

        // Act
        adminService.deleteUser(1L, "admin");

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).findByUsername("admin");
        verify(userRepository).delete(testUser);
    }

    @Test
    void should_ThrowException_When_DeleteUser_AdminTriesToDeleteSelf() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testAdmin));

        // Act & Assert
        assertThrows(AdminCannotDeleteSelfException.class, () -> adminService.deleteUser(2L, "admin"));
        verify(userRepository).findById(2L);
        verify(userRepository).findByUsername("admin");
        verify(userRepository, never()).delete(any());
    }

    @Test
    void should_ThrowException_When_DeleteUser_LastAdmin() {
        // Arrange
        User otherAdmin = User.builder()
                .username("otheradmin")
                .password("password")
                .role(UserRole.ADMIN)
                .build();
        otherAdmin.setId(3L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
        when(userRepository.findByUsername("otheradmin")).thenReturn(Optional.of(otherAdmin));
        when(userRepository.findAll()).thenReturn(Arrays.asList(testAdmin));

        // Act & Assert
        assertThrows(AdminCannotDeleteLastAdminException.class, () -> adminService.deleteUser(2L, "otheradmin"));
        verify(userRepository).findById(2L);
        verify(userRepository).findByUsername("otheradmin");
        verify(userRepository, never()).delete(any());
    }

    @Test
    void should_DeleteAdmin_When_MultipleAdminsExist() {
        // Arrange
        User otherAdmin = User.builder()
                .username("otheradmin")
                .password("password")
                .role(UserRole.ADMIN)
                .build();
        otherAdmin.setId(3L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
        when(userRepository.findByUsername("otheradmin")).thenReturn(Optional.of(otherAdmin));
        when(userRepository.findAll()).thenReturn(Arrays.asList(testAdmin, otherAdmin));

        // Act
        adminService.deleteUser(2L, "otheradmin");

        // Assert
        verify(userRepository).findById(2L);
        verify(userRepository).findByUsername("otheradmin");
        verify(userRepository).delete(testAdmin);
    }

    @Test
    void should_ThrowException_When_DeleteUser_AdminNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("nonexistentadmin")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> adminService.deleteUser(1L, "nonexistentadmin"));
        verify(userRepository).findById(1L);
        verify(userRepository).findByUsername("nonexistentadmin");
        verify(userRepository, never()).delete(any());
    }
}
