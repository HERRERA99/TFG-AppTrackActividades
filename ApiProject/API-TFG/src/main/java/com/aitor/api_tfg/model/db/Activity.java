package com.aitor.api_tfg.model.db;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    @Id
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    private Modalidades activityType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    private float distance;
    private long duration;
    private double positiveElevation;
    private float averageSpeed;
    private float calories;
    private float maxSpeed;

    @ElementCollection
    @CollectionTable(
            name = "activity_speeds",
            joinColumns = @JoinColumn(name = "activity_id")
    )
    @OrderColumn(name = "speed_order") // Columna para el índice
    @Column(name = "speed_value")
    private List<Float> speeds;

    @ElementCollection
    @CollectionTable(
            name = "activity_elevations",
            joinColumns = @JoinColumn(name = "activity_id")
    )
    @OrderColumn(name = "elevation_order")
    @Column(name = "elevation_value")
    private List<Double> elevations;

    @ElementCollection
    @CollectionTable(
            name = "activity_route",
            joinColumns = @JoinColumn(name = "activity_id")
    )
    @OrderColumn(name = "route_order") // Índice para coordenadas
    private List<LatLng> route;

    @ElementCollection
    @CollectionTable(
            name = "activity_distances",
            joinColumns = @JoinColumn(name = "activity_id")
    )
    @OrderColumn(name = "distances_order")
    private List<Float> distances;

    private double maxAltitude;


    private String title;
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}