package com.aitor.api_tfg.model.dto;

import com.aitor.api_tfg.model.db.LatLng;
import com.aitor.api_tfg.model.db.Modalidades;
import com.aitor.api_tfg.model.db.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetupCreateDTO {
    private String title;
    private String description;
    private OffsetDateTime dateTime;
    private String location;
    private Integer maxParticipants;
    private LatLng locationCoordinates;
    private Modalidades sportType;
}
