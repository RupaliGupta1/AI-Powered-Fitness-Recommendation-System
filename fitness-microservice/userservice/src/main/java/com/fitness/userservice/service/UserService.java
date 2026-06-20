package com.fitness.userservice.service;

import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository=userRepository;
    }
    public  UserResponse getUserProfile(String userId) {

      User user= userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found with id "+userId));

        UserResponse userResponse=new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setPassword(user.getPassword());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return userResponse;
    }
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .keyCloakId(user.getKeyCloakId())
                .password(user.getPassword()) // ⚠️ optional (see below)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    public UserResponse register(RegisterRequest request) {

        // 1. Check by keycloakId
        User existingByKeycloak = userRepository.findByKeyCloakId(request.getKeyCloakId());
        if (existingByKeycloak != null) {
            return mapToResponse(existingByKeycloak);
        }

        // 2. Check by email
        User existingByEmail = userRepository.findByEmail(request.getEmail());
        if (existingByEmail != null) {
            // attach keycloakId if missing
            existingByEmail.setKeyCloakId(request.getKeyCloakId());
            User updated = userRepository.save(existingByEmail);
            return mapToResponse(updated);
        }

        // 3. Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setKeyCloakId(request.getKeyCloakId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(request.getPassword());

        User saved = userRepository.save(user); // 🔥 THIS MUST EXECUTE

        return mapToResponse(saved);
    }
    public Boolean existsByUserId(String userId) {
        return userRepository.existsBykeyCloakId(userId);
    }
}
