package com.aitor.api_tfg.services;

import com.aitor.api_tfg.model.db.User;
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

    public UserResponse getUserById(int id) {
        Optional<User> userOptional = userRepository.findById(id);

        return getUserResponse(userOptional);
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
