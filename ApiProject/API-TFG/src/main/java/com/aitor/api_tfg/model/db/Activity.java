package com.aitor.api_tfg.model.db;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
    private OffsetDateTime startTime;

    @Enumerated(EnumType.STRING)
    private Modalidades activityType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private OffsetDateTime endTime;
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
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<Float> speeds;

    @ElementCollection
    @CollectionTable(
            name = "activity_elevations",
            joinColumns = @JoinColumn(name = "activity_id")
    )
    @OrderColumn(name = "elevation_order")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<Double> elevations;

    @ElementCollection
    @CollectionTable(
            name = "activity_route",
            joinColumns = @JoinColumn(name = "activity_id")
    )
    @OrderColumn(name = "route_order")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<LatLng> route;

    @ElementCollection
    @CollectionTable(
            name = "activity_distances",
            joinColumns = @JoinColumn(name = "activity_id")
    )
    @OrderColumn(name = "distances_order")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<Float> distances;

    private double maxAltitude;

    private String title;
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}