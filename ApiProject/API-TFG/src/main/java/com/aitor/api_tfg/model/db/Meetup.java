package com.aitor.api_tfg.model.db;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "meetup")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Meetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    private String location;
    private Double distance;
    private Double elevationGain;

    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "latPuntoQuedada")),
            @AttributeOverride(name = "longitude", column = @Column(name = "lngPuntoQuedada"))
    })
    private LatLng locationCoordinates;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modalidades sportType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    @ToString.Exclude
    private User organizer;

    @ManyToMany
    @JoinTable(
            name = "meetup_participants",
            joinColumns = @JoinColumn(name = "meetup_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    private List<User> participants;

    @ElementCollection
    @CollectionTable(
            name = "meetup_route",
            joinColumns = @JoinColumn(name = "meetup_id")
    )
    @OrderColumn(name = "route_order")
    private List<LatLng> route;

    @PreRemove
    private void removeParticipantsAssociations() {
        if (participants != null) {
            this.participants.clear();
        }
    }

    public void addParticipant(User user) {
        if (!participants.contains(user)) {
            this.participants.add(user);
        }
    }

    public void removeParticipant(User user) {
        this.participants.remove(user);
    }
}

