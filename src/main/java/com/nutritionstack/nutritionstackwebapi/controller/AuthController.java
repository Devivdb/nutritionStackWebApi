package com.nutritionstack.nutritionstackwebapi.controller;

import com.nutritionstack.nutritionstackwebapi.dto.AuthResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.UserLoginRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.UserRegistrationRequestDTO;
import com.nutritionstack.nutritionstackwebapi.service.AuthenticationService;
import com.nutritionstack.nutritionstackwebapi.util.AuthResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody UserRegistrationRequestDTO request) {
        AuthResponseDTO response = authenticationService.registerUser(request);
        return AuthResponseBuilder.authCreated(
                response.getToken(),
                response.getUsername(),
                response.getRole(),
                response.getMessage()
        );
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody UserLoginRequestDTO request) {
        AuthResponseDTO response = authenticationService.loginUser(request);
        return AuthResponseBuilder.authSuccess(
                response.getToken(),
                response.getUsername(),
                response.getRole(),
                response.getMessage()
        );
    }
}
