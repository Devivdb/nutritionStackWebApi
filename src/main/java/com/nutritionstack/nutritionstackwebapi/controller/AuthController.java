package com.nutritionstack.nutritionstackwebapi.controller;

import com.nutritionstack.nutritionstackwebapi.dto.AuthResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.UserLoginRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.UserRegistrationRequestDTO;
import com.nutritionstack.nutritionstackwebapi.service.UserService;
import com.nutritionstack.nutritionstackwebapi.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody UserRegistrationRequestDTO request) {
        AuthResponseDTO response = userService.registerUser(request);
        return ResponseBuilder.authCreated(
                response.getToken(),
                response.getUsername(),
                response.getRole(),
                response.getMessage()
        );
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody UserLoginRequestDTO request) {
        AuthResponseDTO response = userService.loginUser(request);
        return ResponseBuilder.authSuccess(
                response.getToken(),
                response.getUsername(),
                response.getRole(),
                response.getMessage()
        );
    }
}
