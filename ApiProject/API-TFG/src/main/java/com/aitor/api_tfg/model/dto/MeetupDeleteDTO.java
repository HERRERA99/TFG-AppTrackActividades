package com.aitor.api_tfg.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetupDeleteDTO {
    private Long meetupId;
    private Integer organizerId;
    private LocalDateTime dateTime;
}
