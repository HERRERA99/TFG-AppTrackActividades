package com.aitor.api_tfg.model.request;

import com.aitor.api_tfg.model.db.Gender;
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
    Double weight;
    Integer height;
    LocalDate birthdate;
    Gender gender;
}
