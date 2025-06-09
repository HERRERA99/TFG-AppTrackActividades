package com.aitor.api_tfg.model.dto;

import com.aitor.api_tfg.model.db.Modalidades;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetupItemListDTO {
    private long id;
    private String title;
    private LocalDateTime dateTime;
    private String location;
    private Modalidades sportType;
    private boolean isParticipating;
}
