package com.aitor.api_tfg.model.db;

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

    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    private Modalidades activityType;

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
    @OrderColumn(name = "speed_order")
    private List<Float> speeds;

    @ElementCollection
    @CollectionTable(
            name = "activity_elevations",
            joinColumns = @JoinColumn(name = "activity_id")
    )
    @OrderColumn(name = "elevation_order")
    private List<Double> elevations;

    @ElementCollection
    @CollectionTable(
            name = "activity_route",
            joinColumns = @JoinColumn(name = "activity_id")
    )
    @OrderColumn(name = "route_order")
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
    private boolean publicActivity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}