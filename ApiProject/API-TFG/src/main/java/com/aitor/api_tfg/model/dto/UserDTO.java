package com.aitor.api_tfg.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Integer id;
    private String username;
    private String imageUrl;
}
