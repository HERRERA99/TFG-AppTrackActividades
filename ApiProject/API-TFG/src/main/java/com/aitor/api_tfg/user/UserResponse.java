package com.aitor.api_tfg.user;

import com.aitor.api_tfg.model.user.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String Username;
    private String nombre;
    private String apellidos;
    private String email;
    private Double peso;
    private Integer altura;
    private Gender genero;
}
