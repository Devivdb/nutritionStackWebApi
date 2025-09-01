package com.nutritionstack.nutritionstackwebapi.service.auth;

import com.nutritionstack.nutritionstackwebapi.constant.ErrorMessages;
import com.nutritionstack.nutritionstackwebapi.dto.auth.AuthResponseDTO;
import com.nutritionstack.nutritionstackwebapi.dto.auth.UserLoginRequestDTO;
import com.nutritionstack.nutritionstackwebapi.dto.auth.UserRegistrationRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.InvalidCredentialsException;
import com.nutritionstack.nutritionstackwebapi.exception.UserAlreadyExistsException;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.model.auth.UserRole;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthResponseDTO registerUser(UserRegistrationRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(request.getUsername());
        }
        
        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                UserRole.USER
        );
        
        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        
        return new AuthResponseDTO(
                token,
                savedUser.getUsername(),
                savedUser.getRole(),
                ErrorMessages.USER_REGISTERED_SUCCESSFULLY
        );
    }
    
    public AuthResponseDTO loginUser(UserLoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(ErrorMessages.USER_NOT_FOUND, request.getUsername())));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        
        String token = jwtService.generateToken(user);
        
        return new AuthResponseDTO(
                token,
                user.getUsername(),
                user.getRole(),
                ErrorMessages.LOGIN_SUCCESSFUL
        );
    }
}
