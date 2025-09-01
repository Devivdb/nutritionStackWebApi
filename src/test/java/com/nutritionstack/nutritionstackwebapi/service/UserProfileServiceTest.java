package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.user.UserProfileDTO;
import com.nutritionstack.nutritionstackwebapi.dto.user.UserProfileUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.UserAlreadyExistsException;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.auth.UserRole;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import com.nutritionstack.nutritionstackwebapi.service.user.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserProfileService userProfileService;
    
    private User testUser;
    private UserProfileUpdateRequestDTO updateRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "encodedPassword", UserRole.USER);
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
        
        updateRequest = new UserProfileUpdateRequestDTO("newusername", "newpassword123");
    }
    
    @Test
    void getUserProfile_ShouldReturnProfile_WhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        UserProfileDTO profile = userProfileService.getUserProfile("testuser");

        assertNotNull(profile);
        assertEquals(1L, profile.getId());
        assertEquals("testuser", profile.getUsername());
        assertEquals(UserRole.USER, profile.getRole());
        assertEquals(testUser.getCreatedAt(), profile.getCreatedAt());
        
        verify(userRepository).findByUsername("testuser");
    }
    
    @Test
    void getUserProfile_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userProfileService.getUserProfile("testuser");
        });
        
        assertEquals("User not found: testuser", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
    }
    
    @Test
    void updateUserProfile_ShouldUpdateBothFields_WhenBothProvided() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(passwordEncoder.encode("newpassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        UserProfileDTO updatedProfile = userProfileService.updateUserProfile("testuser", updateRequest);
        
        assertNotNull(updatedProfile);
        assertEquals("newusername", updatedProfile.getUsername());
        
        verify(userRepository).findByUsername("testuser");
        verify(userRepository).existsByUsername("newusername");
        verify(passwordEncoder).encode("newpassword123");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void updateUserProfile_ShouldUpdateOnlyUsername_WhenOnlyUsernameProvided() {
        UserProfileUpdateRequestDTO usernameOnlyRequest = new UserProfileUpdateRequestDTO("newusername", null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        UserProfileDTO updatedProfile = userProfileService.updateUserProfile("testuser", usernameOnlyRequest);
        
        assertNotNull(updatedProfile);
        
        verify(userRepository).findByUsername("testuser");
        verify(userRepository).existsByUsername("newusername");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void updateUserProfile_ShouldUpdateOnlyPassword_WhenOnlyPasswordProvided() {
        UserProfileUpdateRequestDTO passwordOnlyRequest = new UserProfileUpdateRequestDTO(null, "newpassword123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        UserProfileDTO updatedProfile = userProfileService.updateUserProfile("testuser", passwordOnlyRequest);
        
        assertNotNull(updatedProfile);
        
        verify(userRepository).findByUsername("testuser");
        verify(userRepository, never()).existsByUsername(anyString());
        verify(passwordEncoder).encode("newpassword123");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void updateUserProfile_ShouldNotUpdate_WhenNoFieldsProvided() {
        UserProfileUpdateRequestDTO emptyRequest = new UserProfileUpdateRequestDTO(null, null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        UserProfileDTO updatedProfile = userProfileService.updateUserProfile("testuser", emptyRequest);
        
        assertNotNull(updatedProfile);
        
        verify(userRepository).findByUsername("testuser");
        verify(userRepository, never()).existsByUsername(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void updateUserProfile_ShouldThrowException_WhenUsernameAlreadyExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(true);
        
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userProfileService.updateUserProfile("testuser", updateRequest);
        });
        
        assertEquals("User with username 'newusername' already exists", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(userRepository).existsByUsername("newusername");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void deleteUserAccount_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        userProfileService.deleteUserAccount("testuser");
        
        verify(userRepository).findByUsername("testuser");
        verify(userRepository).delete(testUser);
    }
    
    @Test
    void deleteUserAccount_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userProfileService.deleteUserAccount("testuser");
        });
        
        assertEquals("User not found: testuser", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(userRepository, never()).delete(any(User.class));
    }
}
