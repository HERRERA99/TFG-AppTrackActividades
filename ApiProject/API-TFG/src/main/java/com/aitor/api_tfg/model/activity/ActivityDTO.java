package com.aitor.api_tfg.model.activity;

import com.aitor.api_tfg.model.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO {
    private Long id;
    private LocalDateTime startTime;
    private String activityType;
    private LocalDateTime endTime;
    private float distance;
    private long duration;
    private double positiveElevation;
    private float averageSpeed;
    private float calories;
    private float maxSpeed;
    private List<Float> speeds;
    private List<Double> elevations;
    private double maxAltitude;
    private List<LatLngDTO> route;
    private String title;
    private boolean isPublic;
}

