package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.constant.ErrorMessages;
import com.nutritionstack.nutritionstackwebapi.dto.AuthResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.UserLoginRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.UserRegistrationRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.InvalidCredentialsException;
import com.nutritionstack.nutritionstackwebapi.exception.UserAlreadyExistsException;
import com.nutritionstack.nutritionstackwebapi.model.User;
import com.nutritionstack.nutritionstackwebapi.model.UserRole;
import com.nutritionstack.nutritionstackwebapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private UserRegistrationRequestDTO registrationRequest;
    private UserLoginRequestDTO loginRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "encodedPassword", UserRole.USER);
        testUser.setId(1L);
        
        registrationRequest = new UserRegistrationRequestDTO("testuser", "password123");
        
        loginRequest = new UserLoginRequestDTO("testuser", "password123");
    }
    
    @Test
    void registerUser_ShouldCreateNewUser_WhenValidRequest() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");
        
        // Act
        AuthResponseDTO response = userService.registerUser(registrationRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals(UserRole.USER, response.getRole());
        assertEquals(ErrorMessages.USER_REGISTERED_SUCCESSFULLY, response.getMessage());
        
        verify(userRepository).existsByUsername("testuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(testUser);
    }
    
    @Test
    void registerUser_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(registrationRequest);
        });
        
        assertEquals("User with username 'testuser' already exists", exception.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void loginUser_ShouldReturnAuthResponse_WhenValidCredentials() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");
        
        // Act
        AuthResponseDTO response = userService.loginUser(loginRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals(UserRole.USER, response.getRole());
        assertEquals(ErrorMessages.LOGIN_SUCCESSFUL, response.getMessage());
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateToken(testUser);
    }
    
    @Test
    void loginUser_ShouldThrowInvalidCredentialsException_WhenInvalidPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);
        
        UserLoginRequestDTO invalidLoginRequest = new UserLoginRequestDTO("testuser", "wrongpassword");
        
        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUser(invalidLoginRequest);
        });
        
        assertEquals(ErrorMessages.INVALID_CREDENTIALS, exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
        verify(jwtService, never()).generateToken(any(User.class));
    }
    
    @Test
    void userService_ShouldBeInstantiated() {
        // This test verifies that our UserService can be instantiated
        assertNotNull(userService);
    }
}
