package com.aitor.api_tfg.model.dto;

import com.aitor.api_tfg.model.db.LatLng;
import com.aitor.api_tfg.model.db.Modalidades;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicationDTO {
    private Long id;

    private UserDTO user;

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private OffsetDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private OffsetDateTime endTime;

    private Modalidades activityType;
    private float distance;
    private long duration;
    private double positiveElevation;
    private float averageSpeed;
    private float calories;
    private float maxSpeed;
    private List<Float> speeds;
    private List<Double> elevations;
    private List<LatLng> route;
    private List<Float> distances;
    private double maxAltitude;

    private List<Integer> likes;
}
