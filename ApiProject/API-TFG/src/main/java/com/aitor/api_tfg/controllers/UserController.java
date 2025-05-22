package com.aitor.api_tfg.controllers;

import com.aitor.api_tfg.model.dto.PageDTO;
import com.aitor.api_tfg.model.dto.PageInfoDTO;
import com.aitor.api_tfg.model.dto.UserProfileDTO;
import com.aitor.api_tfg.model.dto.UserSearchDTO;
import com.aitor.api_tfg.model.response.UserResponse;
import com.aitor.api_tfg.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyUserInfo(Authentication authentication) {
        UserResponse user = userService.getUserByName(authentication.getName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable int idUser, Authentication authentication) {
        String currentUsername = authentication.getName();
        UserProfileDTO user = userService.getUserById(idUser, currentUsername);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<PageDTO<UserSearchDTO>> searchUsers(
            @RequestParam String text,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        if (page < 1) page = 1;
        if (size < 1) size = 10;

        // Spring Data usa base 0
        Page<UserSearchDTO> usersPage = userService.searchUsersByText(text, page - 1, size);

        // Construir base URL sin parámetros
        String baseUrl = request.getRequestURL().toString();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("text", text)
                .queryParam("size", size);

        String nextUrl = null;
        if (page < usersPage.getTotalPages()) {
            nextUrl = uriBuilder.replaceQueryParam("page", page + 1).toUriString();
        }

        String prevUrl = null;
        if (page > 1) {
            prevUrl = uriBuilder.replaceQueryParam("page", page - 1).toUriString();
        }

        PageInfoDTO pageInfo = new PageInfoDTO(
                (int) usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                nextUrl,
                prevUrl
        );

        PageDTO<UserSearchDTO> response = new PageDTO<>(pageInfo, usersPage.getContent());

        return ResponseEntity.ok(response);
    }

}
