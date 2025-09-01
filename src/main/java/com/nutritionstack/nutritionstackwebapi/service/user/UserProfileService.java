package com.nutritionstack.nutritionstackwebapi.service.user;

import com.nutritionstack.nutritionstackwebapi.dto.user.UserProfileDTO;
import com.nutritionstack.nutritionstackwebapi.dto.user.UserProfileUpdateRequestDTO;
import com.nutritionstack.nutritionstackwebapi.exception.UserAlreadyExistsException;
import com.nutritionstack.nutritionstackwebapi.model.auth.User;
import com.nutritionstack.nutritionstackwebapi.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserProfileDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found: %s", username)));
        
        return mapToUserProfileDTO(user);
    }
    
    public UserProfileDTO updateUserProfile(String currentUsername, UserProfileUpdateRequestDTO request) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found: %s", currentUsername)));

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            if (!currentUsername.equals(request.getUsername()) && 
                userRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException(request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return mapToUserProfileDTO(updatedUser);
    }
    
    public void deleteUserAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found: %s", username)));
        
        userRepository.delete(user);
    }
    
    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found: %s", username)));
        
        return user.getId();
    }
    
    private UserProfileDTO mapToUserProfileDTO(User user) {
        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setRole(user.getRole());
        profile.setCreatedAt(user.getCreatedAt());
        return profile;
    }
}
