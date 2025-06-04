package com.aitor.api_tfg.controllers;

import com.aitor.api_tfg.model.db.Meetup;
import com.aitor.api_tfg.model.dto.MeetupCreateDTO;
import com.aitor.api_tfg.model.response.ErrorResponse;
import com.aitor.api_tfg.services.MeetupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/meetups")
@RequiredArgsConstructor
public class MeetupController {

    private final MeetupService meetupService;

    @PostMapping
    public ResponseEntity<?> createMeetup(@RequestBody MeetupCreateDTO meetup,
                                          Authentication authentication,
                                          HttpServletRequest request) {
        try {
            String organizerName = authentication.getName();
            Meetup meetupResponse = meetupService.createMeerup(organizerName, meetup);

            return new ResponseEntity<>(meetupResponse, HttpStatus.CREATED);

        } catch (IllegalArgumentException ex) {
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "Se ha producido un error inesperado.",
                    request.getRequestURI()
            );
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
