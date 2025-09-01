package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.constant.ErrorMessages;
import com.nutritionstack.nutritionstackwebapi.dto.auth.AuthResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.auth.UserLoginRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.auth.UserRegistrationRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.InvalidCredentialsException;
import com.nutritionstack.nutritionstackwebapi.exception.UserAlreadyExistsException;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.auth.UserRole;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import com.nutritionstack.nutritionstackwebapi.service.auth.AuthenticationService;
import com.nutritionstack.nutritionstackwebapi.service.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @InjectMocks
    private AuthenticationService authenticationService;
    
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
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");

        AuthResponseDTO response = authenticationService.registerUser(registrationRequest);

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
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authenticationService.registerUser(registrationRequest);
        });
        
        assertEquals("User with username 'testuser' already exists", exception.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void loginUser_ShouldReturnToken_WhenValidCredentials() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");

        AuthResponseDTO response = authenticationService.loginUser(loginRequest);

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
    void loginUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.loginUser(loginRequest);
        });
        
        assertEquals("User not found: testuser", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
    
    @Test
    void loginUser_ShouldThrowException_WhenInvalidPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.loginUser(loginRequest);
        });
        
        assertNotNull(exception);
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService, never()).generateToken(any(User.class));
    }
}
