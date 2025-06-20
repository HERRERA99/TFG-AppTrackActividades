package com.aitor.api_tfg.model.db;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class LatLng implements Serializable {
    private double latitude;
    private double longitude;

    protected LatLng() {}
}
