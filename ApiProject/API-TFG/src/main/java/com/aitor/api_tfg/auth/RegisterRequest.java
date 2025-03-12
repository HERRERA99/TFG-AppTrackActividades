package com.aitor.api_tfg.auth;

import com.aitor.api_tfg.user.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    Integer id;
    String username;
    String email;
    String password;
    String firstname;
    String lastname;
    LocalDate birthdate;
    Gender gender;
}
