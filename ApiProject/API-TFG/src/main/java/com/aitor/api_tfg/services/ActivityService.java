package com.aitor.api_tfg.services;

import com.aitor.api_tfg.mappers.ActivityMapper;
import com.aitor.api_tfg.model.dto.ActivityDTO;
import com.aitor.api_tfg.model.dto.LatLngDTO;
import com.aitor.api_tfg.model.db.Activity;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.repositories.UserRepository;
import com.aitor.api_tfg.repositories.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;


    public List<ActivityDTO> getActivities() {
        List<Activity> activities = activityRepository.findAll();

        if (activities.isEmpty()) {
            throw new NoSuchElementException("No hay actividades registradas");
        }

        return activities.stream().map(a ->
                new ActivityDTO(
                        a.getId(),
                        a.getStartTime(),
                        a.getActivityType() != null ? a.getActivityType().toString() : null,
                        a.getEndTime(),
                        a.getDistance(),
                        a.getDuration(),
                        a.getPositiveElevation(),
                        a.getAverageSpeed(),
                        a.getCalories(),
                        a.getMaxSpeed(),
                        a.getSpeeds(),
                        a.getElevations(),
                        a.getMaxAltitude(),
                        a.getRoute() != null ?
                                a.getRoute().stream().map(r ->
                                        new LatLngDTO(r.getLatitude(), r.getLongitude())
                                ).collect(Collectors.toList()) :
                                List.of(),
                        a.getDistances(),
                        a.getTitle(),
                        a.isPublicActivity()
                )
        ).collect(Collectors.toList());
    }

    public ActivityDTO createActivity(ActivityDTO activityDTO, String username) {
        if (activityDTO == null || username == null || username.isBlank()) {
            throw new IllegalArgumentException("Datos de actividad o nombre de usuario inválidos");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        try {
            Activity activity = activityMapper.mapToActivityEntity(activityDTO, user);
            Activity createdActivity = activityRepository.save(activity);
            return activityMapper.mapToActivityDTO(createdActivity);
        } catch (Exception e) {
            throw new IllegalStateException("Error al crear la actividad: " + e.getMessage(), e);
        }
    }
}

