package com.aitor.api_tfg.controllers;

import com.aitor.api_tfg.model.db.Meetup;
import com.aitor.api_tfg.model.dto.MeetupCreateDTO;
import com.aitor.api_tfg.model.dto.MeetupResponseDTO;
import com.aitor.api_tfg.model.response.ErrorResponse;
import com.aitor.api_tfg.services.MeetupService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.TimeZone;

@RestController
@RequestMapping("/meetups")
@RequiredArgsConstructor
public class MeetupController {

    private final MeetupService meetupService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMeetup(
            @RequestPart("meetup") String meetupJson,
            @RequestPart(value = "gpxFile", required = false) MultipartFile gpxFile,
            Authentication authentication,
            HttpServletRequest request
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            MeetupCreateDTO meetup = objectMapper.readValue(meetupJson, MeetupCreateDTO.class);

            String organizerName = authentication.getName();
            MeetupResponseDTO createdMeetup = meetupService.createMeerup(organizerName, meetup, gpxFile);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeetup);
        } catch (IllegalArgumentException ex) {
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.badRequest().body(error);
        } catch (Exception ex) {
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "Error inesperado: " + ex.getMessage(),
                    request.getRequestURI()
            );
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMeetupById(@PathVariable Long id) {
        return meetupService.getMeetupById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
