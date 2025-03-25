package com.aitor.api_tfg.activity;

import com.aitor.api_tfg.model.activity.*;
import com.aitor.api_tfg.model.user.User;
import com.aitor.api_tfg.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public ActivityDTO createActivity(ActivityDTO activityDTO, String username) {
        User user = userRepository.findByUsername(username).orElseThrow( () -> new RuntimeException("User not found"));
        
        Activity activity = mapToEntity(activityDTO, user);

        Activity createdActivity = activityRepository.save(activity);

        return mapToDTO(createdActivity);
    }

    private Activity mapToEntity(ActivityDTO activityDTO, User user) {
        return Activity.builder()
                .startTime(activityDTO.getStartTime())
                .activityType(Modalidades.valueOf(activityDTO.getActivityType()))
                .endTime(activityDTO.getEndTime())
                .distance(activityDTO.getDistance())
                .duration(activityDTO.getDuration())
                .positiveElevation(activityDTO.getPositiveElevation())
                .averageSpeed(activityDTO.getAverageSpeed())
                .calories(activityDTO.getCalories())
                .maxSpeed(activityDTO.getMaxSpeed())
                .speeds(activityDTO.getSpeeds())
                .elevations(activityDTO.getElevations())
                .maxAltitude(activityDTO.getMaxAltitude())
                .route(convertLatLngDTOs(activityDTO.getRoute()))
                .title(activityDTO.getTitle())
                .isPublic(activityDTO.isPublic())
                .user(user)
                .build();
    }

    private List<LatLng> convertLatLngDTOs(List<LatLngDTO> dtos) {
        return dtos.stream()
                .map(dto -> new LatLng(dto.getLatitude(), dto.getLongitude()))
                .collect(Collectors.toList());
    }

    private ActivityDTO mapToDTO(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getId())
                .startTime(activity.getStartTime())
                .activityType(activity.getActivityType().name())
                .endTime(activity.getEndTime())
                .distance(activity.getDistance())
                .duration(activity.getDuration())
                .positiveElevation(activity.getPositiveElevation())
                .averageSpeed(activity.getAverageSpeed())
                .calories(activity.getCalories())
                .maxSpeed(activity.getMaxSpeed())
                .speeds(activity.getSpeeds())
                .elevations(activity.getElevations())
                .maxAltitude(activity.getMaxAltitude())
                .route(convertLatLngs(activity.getRoute()))
                .title(activity.getTitle())
                .isPublic(activity.isPublic())
                .build();
    }

    private List<LatLngDTO> convertLatLngs(List<LatLng> latLngs) {
        return latLngs.stream()
                .map(l -> new LatLngDTO(l.getLatitude(), l.getLongitude()))
                .collect(Collectors.toList());
    }
}
