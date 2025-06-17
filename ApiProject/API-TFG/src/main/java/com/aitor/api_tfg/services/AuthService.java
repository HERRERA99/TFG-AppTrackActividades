package com.aitor.api_tfg.services;

import com.aitor.api_tfg.model.response.AuthResponse;
import com.aitor.api_tfg.model.request.LoginRequest;
import com.aitor.api_tfg.model.request.RegisterRequest;
import com.aitor.api_tfg.model.response.ValidResponse;
import com.aitor.api_tfg.security.JwtService;
import com.aitor.api_tfg.model.db.Role;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!user.isEnabled()) {
            throw new IllegalStateException("Debes verificar tu correo electrónico antes de iniciar sesión");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        String token = UUID.randomUUID().toString();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .height(request.getHeight())
                .weight(request.getWeight())
                .role(Role.USER)
                .birthdate(request.getBirthdate())
                .gender(request.getGender())
                .imageUrl("https://i.postimg.cc/RFSkJZtg/462076-1g-CSN462076-MG3928385-1248x702.webp")
                .enabled(false)
                .verificationToken(token)
                .build();

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), token);

        return AuthResponse.builder()
                .token(null)
                .build();
    }


    public ValidResponse isTokenEnable(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isValid = jwtService.isTokenValid(jwtService.getToken(userDetails), userDetails);

        return ValidResponse.builder()
                .isValid(isValid)
                .build();
    }

    public ResponseEntity<String> verifyUserAccount(String token) {
        Optional<User> userOptional = userRepository.findByVerificationToken(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/html")
                    .body(getHtmlResponse("❌ Token inválido o expirado", "#F24822"));
        }

        User user = userOptional.get();
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return ResponseEntity
                .ok()
                .header("Content-Type", "text/html")
                .body(getHtmlResponse("✅ Cuenta verificada correctamente. Ya puedes iniciar sesión.", "#4CAF50"));
    }


    private String getHtmlResponse(String message, String color) {
        return """
        <html>
        <head>
            <meta charset="UTF-8">
            <title>Verificación de Cuenta</title>
        </head>
        <body style="font-family: 'Segoe UI', sans-serif; background-color: #f9f9f9; margin: 0; padding: 0;">
            <div style="max-width: 600px; margin: 50px auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); text-align: center;">
                <img src="https://i.postimg.cc/vBMsG01L/Logo-Track-Fit.png" alt="Logo" style="width: 80px; margin-bottom: 20px;">
                <h2 style="color: %s;">%s</h2>
            </div>
        </body>
        </html>
        """.formatted(color, message);
    }
}
