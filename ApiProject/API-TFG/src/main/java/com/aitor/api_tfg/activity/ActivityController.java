package com.aitor.api_tfg.activity;

import com.aitor.api_tfg.model.activity.ActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(
            @RequestBody ActivityDTO activityDTO, Authentication authentication) {

        // Asociar automaticamente el usuario autentificado
        String username = authentication.getName();
        ActivityDTO savedActivity = activityService.createActivity(activityDTO, username);

        return ResponseEntity.ok(savedActivity);
    }
}
