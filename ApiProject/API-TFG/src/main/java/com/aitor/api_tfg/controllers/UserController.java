package com.aitor.api_tfg.controllers;

import com.aitor.api_tfg.model.response.UserResponse;
import com.aitor.api_tfg.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<UserResponse> getUserById(@PathVariable int idUser) {
        UserResponse user = userService.getUserById(idUser);
        return ResponseEntity.ok(user);
    }

}
