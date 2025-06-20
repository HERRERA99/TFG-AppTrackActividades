package com.aitor.api_tfg.controllers;

import com.aitor.api_tfg.model.db.Meetup;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.model.dto.*;
import com.aitor.api_tfg.model.response.ErrorResponse;
import com.aitor.api_tfg.model.response.UserResponse;
import com.aitor.api_tfg.services.MeetupService;
import com.aitor.api_tfg.services.UserService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/meetups")
@RequiredArgsConstructor
public class MeetupController {

    private final MeetupService meetupService;
    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMeetup(
            @RequestPart("meetup") String meetupJson,
            @RequestPart(value = "gpxFile", required = false) MultipartFile gpxFile,
            Authentication authentication,
            HttpServletRequest request
    ) {
        System.out.println(meetupJson);
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
    public ResponseEntity<?> getMeetupById(@PathVariable Long id, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        return ResponseEntity.ok(meetupService.getMeetupById(id, user));
    }

    @GetMapping("/all")
    public ResponseEntity<PageDTO<MeetupItemListDTO>> getNearbyMeetups(
            Authentication authentication,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Meetup> meetupPage = meetupService.getMeetupsOrderedByDistance(lat, lng, pageable);

        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username);

        List<MeetupItemListDTO> content = meetupPage.getContent().stream()
                .map(meetup -> convertToItemListDto(meetup, currentUser))
                .toList();

        String baseUrl = request.getRequestURL().toString();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("lat", lat)
                .queryParam("lng", lng)
                .queryParam("size", size);

        String nextUrl = null;
        if (page < meetupPage.getTotalPages()) {
            nextUrl = uriBuilder.replaceQueryParam("page", page + 1).toUriString();
        }

        String prevUrl = null;
        if (page > 1) {
            prevUrl = uriBuilder.replaceQueryParam("page", page - 1).toUriString();
        }

        PageInfoDTO pageInfo = new PageInfoDTO(
                (int) meetupPage.getTotalElements(),
                meetupPage.getTotalPages(),
                nextUrl,
                prevUrl
        );

        PageDTO<MeetupItemListDTO> response = new PageDTO<>(pageInfo, content);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<PageDTO<MeetupItemListDTO>> getMyMeetupsOrderedByDistance(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        Pageable pageable = PageRequest.of(page - 1, size);
        User currentUser = userService.findUserByUsername(authentication.getName());

        Page<Meetup> meetupPage = meetupService.getUserMeetupsOrderedByDistance(currentUser.getId(), pageable);

        List<MeetupItemListDTO> content = meetupPage.getContent().stream()
                .map(meetup -> convertToItemListDto(meetup, currentUser))
                .toList();

        String baseUrl = request.getRequestURL().toString();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("size", size);

        String nextUrl = (page < meetupPage.getTotalPages()) ?
                uriBuilder.replaceQueryParam("page", page + 1).toUriString() : null;
        String prevUrl = (page > 1) ?
                uriBuilder.replaceQueryParam("page", page - 1).toUriString() : null;

        PageInfoDTO pageInfo = new PageInfoDTO(
                (int) meetupPage.getTotalElements(),
                meetupPage.getTotalPages(),
                nextUrl,
                prevUrl
        );

        PageDTO<MeetupItemListDTO> response = new PageDTO<>(pageInfo, content);
        return ResponseEntity.ok(response);
    }

    private MeetupItemListDTO convertToItemListDto(Meetup meetup, User currentUser) {
        boolean isParticipating = meetup.getParticipants().contains(currentUser);
        boolean isOrganizer = meetup.getOrganizer().getId().equals(currentUser.getId());

        return MeetupItemListDTO.builder()
                .id(meetup.getId())
                .title(meetup.getTitle())
                .dateTime(meetup.getDateTime())
                .location(meetup.getLocation())
                .sportType(meetup.getSportType())
                .isParticipating(isParticipating)
                .isOrganizer(isOrganizer)
                .build();
    }


    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinMeetup(@PathVariable Long id, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        MeetupResponseDTO response = meetupService.joinMeetup(id, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<?> leaveMeetup(@PathVariable Long id, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        MeetupResponseDTO response = meetupService.leaveMeetup(id, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeetup(@PathVariable Long id, Authentication authentication) {
        User user = userService.findUserByUsername(authentication.getName());
        MeetupDeleteDTO meetupDelete = meetupService.deleteMeetup(id, user);
        return ResponseEntity.ok(meetupDelete);
    }
}
