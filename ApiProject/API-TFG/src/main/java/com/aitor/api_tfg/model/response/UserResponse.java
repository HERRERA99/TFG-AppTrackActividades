package com.aitor.api_tfg.model.response;

import com.aitor.api_tfg.model.db.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String image;
    private String Username;
    private String nombre;
    private String apellidos;
    private String email;
    private Double peso;
    private Integer altura;
    private Gender genero;
}
