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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        UserDetails userDetails = userRepository.findByUsernameOrEmail(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(), request.getPassword())
        );

        String token = jwtService.getToken(userDetails);
        return AuthResponse.builder()
                .token(token)
                .build();
    }



    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .imageUrl("https://i.postimg.cc/RFSkJZtg/462076-1g-CSN462076-MG3928385-1248x702.webp")
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .height(request.getHeight())
                .weight(request.getWeight())
                .role(Role.USER)
                .birthdate(request.getBirthdate())
                .gender(request.getGender())
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }

    public ValidResponse isTokenEnable(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean isValid = jwtService.isTokenValid(jwtService.getToken(userDetails), userDetails);

        return ValidResponse.builder()
                .isValid(isValid)
                .build();
    }

}
