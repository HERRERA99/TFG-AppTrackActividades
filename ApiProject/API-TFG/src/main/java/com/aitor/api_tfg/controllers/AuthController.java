package com.aitor.api_tfg.controllers;

import com.aitor.api_tfg.model.request.LoginRequest;
import com.aitor.api_tfg.model.request.RegisterRequest;
import com.aitor.api_tfg.model.response.AuthResponse;
import com.aitor.api_tfg.model.response.ValidResponse;
import com.aitor.api_tfg.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        // Verifica primero si hay un error de expiración
        if (request.getAttribute("expired") != null) {
            return ResponseEntity.ok(new ValidResponse(false, "Token expired"));
        }

        // Verifica si hay un error de token inválido
        if (request.getAttribute("invalid") != null) {
            return ResponseEntity.ok(new ValidResponse(false, "Invalid token"));
        }

        // Si no hay errores, verifica la autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok(new ValidResponse(true, "Token valid"));
        }

        return ResponseEntity.ok(new ValidResponse(false, "No valid token found"));
    }
}
