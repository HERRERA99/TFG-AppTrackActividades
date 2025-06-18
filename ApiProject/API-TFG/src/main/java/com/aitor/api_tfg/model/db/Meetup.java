package com.aitor.api_tfg.model.db;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime dateTime;

    private String location;
    private Double distance;
    private Double elevationGain;

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
            joinColumns = @JoinColumn(name = "meetup_id")
    )
    @OrderColumn(name = "route_order")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<LatLng> route;

    @PreRemove
    private void removeParticipantsAssociations() {
        this.participants.clear();
    }

    public void addParticipant(User user) {
        this.participants.add(user);
    }

    public void removeParticipant(User user) {
        this.participants.remove(user);
    }
}
