package com.aitor.api_tfg.services;

import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.model.dto.UserProfileDTO;
import com.aitor.api_tfg.repositories.UserRepository;
import com.aitor.api_tfg.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getUserByName(String name) {
        Optional<User> userOptional = userRepository.findByUsername(name);

        return getUserResponse(userOptional);
    }

    public UserProfileDTO getUserById(int id, String currentUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Usuario actual no encontrado"));

        boolean isFollowing = currentUser.getFollowing().contains(user);

        return UserProfileDTO.builder()
                .id(user.getId())
                .image(user.getImageUrl())
                .Username(user.getUsername())
                .nombre(user.getFirstname())
                .apellidos(user.getLastname())
                .email(user.getEmail())
                .peso(user.getWeight())
                .altura(user.getHeight())
                .genero(user.getGender())
                .followingCount(user.getFollowing().size())
                .followersCount(user.getFollowers().size())
                .isFollowing(isFollowing)
                .build();
    }


    private UserResponse getUserResponse(Optional<User> userOptional) {
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User user = userOptional.get();
        return UserResponse.builder()
                .id(user.getId())
                .image(user.getImageUrl())
                .Username(user.getUsername())
                .nombre(user.getFirstname())
                .apellidos(user.getLastname())
                .email(user.getEmail())
                .peso(user.getWeight())
                .altura(user.getHeight())
                .genero(user.getGender())
                .build();
    }
}
