package com.aitor.api_tfg.model.dto;

import com.aitor.api_tfg.model.db.LatLng;
import com.aitor.api_tfg.model.db.Modalidades;
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
public class MeetupResponseDTO {
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private Double distance;
    private Double elevationGain;
    private Integer maxParticipants;
    private LatLng locationCoordinates;
    private Modalidades sportType;
    private Integer organizerId;
    private List<UserSearchDTO> participants;
    private List<LatLng> route;
    private boolean isParticipating;
}
