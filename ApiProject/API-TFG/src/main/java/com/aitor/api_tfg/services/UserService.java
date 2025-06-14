package com.aitor.api_tfg.services;

import com.aitor.api_tfg.model.db.Follow;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.model.dto.*;
import com.aitor.api_tfg.repositories.FollowRepository;
import com.aitor.api_tfg.repositories.UserRepository;
import com.aitor.api_tfg.model.response.UserResponse;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final Cloudinary cloudinary;

    public User findUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    public UserResponse getUserByName(String name) {
        Optional<User> userOptional = userRepository.findByUsername(name);

        return getUserResponse(userOptional);
    }

    public UserProfileDTO getUserById(int id, String currentUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Usuario actual no encontrado"));

        // Consultar si currentUser sigue a user
        boolean isFollowing = followRepository.existsByFollowerAndFollowed(currentUser, user);

        // Contar seguidores y seguidos usando el repositorio
        long followersCount = followRepository.countByFollowed(user);
        long followingCount = followRepository.countByFollower(user);

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
                .followingCount((int) followingCount)
                .followersCount((int) followersCount)
                .isFollowing(isFollowing)
                .build();
    }


    public Page<UserSearchDTO> searchUsersByText(String text, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.searchUsersByText(text, pageable);

        return usersPage.map(user -> UserSearchDTO.builder()
                .id(user.getId())
                .image(user.getImageUrl())
                .username(user.getUsername())
                .name(user.getFirstname() + " " + user.getLastname())
                .build());
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

    @Transactional
    public FollowDTO followUser(Integer followedId, String followerUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Follower not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new EntityNotFoundException("Followed user not found"));

        if (followRepository.existsByFollowerAndFollowed(follower, followed)) {
            throw new IllegalStateException("Already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .followed(followed)
                .followedAt(LocalDateTime.now())
                .build();

        followRepository.save(follow);

        return new FollowDTO(follower.getId(), followed.getId());
    }

    @Transactional
    public UnfollowDTO unfollowUser(Integer followedId, String followerUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Follower not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new EntityNotFoundException("Followed user not found"));

        Follow follow = followRepository.findByFollowerAndFollowed(follower, followed)
                .orElseThrow(() -> new IllegalStateException("Not following this user"));

        followRepository.delete(follow);

        return new UnfollowDTO(follower.getId(), followed.getId());
    }

    public UpdateUserDTO updateProfilePicture(int idUser, MultipartFile image, Authentication authentication) throws IOException {
        // Validar usuario autenticado
        User authenticatedUser = (User) authentication.getPrincipal();
        if (authenticatedUser.getId() != idUser) {
            throw new AccessDeniedException("No puedes modificar otro perfil.");
        }

        // Obtener usuario de BD
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Subir imagen a Cloudinary
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) uploadResult.get("secure_url");

        // Actualizar usuario
        user.setImageUrl(imageUrl);
        userRepository.save(user);

        // Devolver DTO
        return UpdateUserDTO.builder()
                .image(imageUrl)
                .build();
    }
}
