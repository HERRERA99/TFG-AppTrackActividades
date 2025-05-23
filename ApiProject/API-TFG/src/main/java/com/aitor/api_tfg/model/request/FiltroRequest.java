package com.aitor.api_tfg.model.request;

import com.aitor.api_tfg.model.db.Modalidades;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FiltroRequest {
    private String nombre;
    private Modalidades activityType;

    private float distanciaMin;
    private float distanciaMax;

    private double positiveElevationMin;
    private double positiveElevationMax;

    private long durationMin;
    private long durationMax;

    private float averageSpeedMin;
    private float averageSpeedMax;
}
