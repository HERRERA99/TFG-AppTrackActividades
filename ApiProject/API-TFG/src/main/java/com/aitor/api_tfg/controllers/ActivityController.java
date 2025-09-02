package com.aitor.api_tfg.controllers;

import com.aitor.api_tfg.services.ActivityService;
import com.aitor.api_tfg.model.dto.ActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(
            @RequestBody ActivityDTO activityDTO, Authentication authentication) {

        String username = authentication.getName();
        ActivityDTO savedActivity = activityService.createActivity(activityDTO, username);

        return ResponseEntity.ok(savedActivity);
    }

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getActivities() {
        List<ActivityDTO> actividades = activityService.getActivities();
        return ResponseEntity.ok(actividades);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public Flux<ActivityDTO> getActivitiesStream() {
        return Flux.fromIterable(activityService.getActivities());
    }
}
