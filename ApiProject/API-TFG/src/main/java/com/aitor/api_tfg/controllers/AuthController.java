package com.aitor.api_tfg.controllers;

import com.aitor.api_tfg.model.request.LoginRequest;
import com.aitor.api_tfg.model.request.RegisterRequest;
import com.aitor.api_tfg.model.response.AuthResponse;
import com.aitor.api_tfg.model.response.ValidResponse;
import com.aitor.api_tfg.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> Login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "register")
    public ResponseEntity<AuthResponse> Register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping(value = "validateToken")
    public ResponseEntity<ValidResponse> validateToken(HttpServletRequest request) {
        ValidResponse response = authService.validateToken(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        return authService.verifyUserAccount(token);
    }
}
