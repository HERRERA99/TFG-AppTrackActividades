package com.aitor.api_tfg.model.db;

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
@Entity
@Table(name = "meetup")
public class Meetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime dateTime;
    private String location;
    private Double distance;
    private Double elevationGain;
    private Integer maxParticipants;
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "latPuntoQuedada")),
            @AttributeOverride(name = "longitude", column = @Column(name = "lngPuntoQuedada"))
    })
    private LatLng locationCoordinates;

    @Enumerated(EnumType.STRING)
    private Modalidades sportType;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizerId;

    @ManyToMany
    @JoinTable(
            name = "meetup_participants",
            joinColumns = @JoinColumn(name = "meetup_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;

    @ElementCollection
    @CollectionTable(
            name = "meetup_route",
            joinColumns = @JoinColumn(name = "activity_id")
    )
    @OrderColumn(name = "route_order")
    private List<LatLng> route;
}
