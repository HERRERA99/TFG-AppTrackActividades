package com.aitor.api_tfg.services;

import com.aitor.api_tfg.model.response.AuthResponse;
import com.aitor.api_tfg.model.request.LoginRequest;
import com.aitor.api_tfg.model.request.RegisterRequest;
import com.aitor.api_tfg.model.response.ValidResponse;
import com.aitor.api_tfg.security.JwtService;
import com.aitor.api_tfg.model.db.Role;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        if (request.getIdentifier() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("El identificador y la contraseña son obligatorios");
        }

        User user = userRepository.findByUsernameOrEmail(request.getIdentifier())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!user.isEnabled()) {
            throw new IllegalStateException("Debes verificar tu correo electrónico antes de iniciar sesión");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public ValidResponse validateToken(HttpServletRequest request) {
        if (request.getAttribute("expired") != null) {
            return new ValidResponse(false, "Token expired");
        }

        if (request.getAttribute("invalid") != null) {
            return new ValidResponse(false, "Invalid token");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return new ValidResponse(true, "Token valid");
        }

        return new ValidResponse(false, "No valid token found");
    }



    public AuthResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("El email, usuario y contraseña son obligatorios");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }

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
                <img src="https://raw.githubusercontent.com/HERRERA99/imagenTFG/refs/heads/main/ic_launcher_foreground.webp" alt="Logo" style="width: 80px; margin-bottom: 20px;">
                <h2 style="color: %s;">%s</h2>
            </div>
        </body>
        </html>
        """.formatted(color, message);
    }
}
