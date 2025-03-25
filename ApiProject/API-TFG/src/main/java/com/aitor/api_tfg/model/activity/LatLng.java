package com.aitor.api_tfg.model.activity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Embeddable
public class LatLng {
    private double latitude;
    private double longitude;

    public LatLng() {} // Requerido por JPA
}