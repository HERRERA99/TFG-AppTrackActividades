package com.aitor.api_tfg.services;

import com.aitor.api_tfg.mappers.ActivityMapper;
import com.aitor.api_tfg.model.dto.ActivityDTO;
import com.aitor.api_tfg.model.dto.LatLngDTO;
import com.aitor.api_tfg.model.db.Activity;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.repositories.UserRepository;
import com.aitor.api_tfg.repositories.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    public List<ActivityDTO> getActivities() {
        return activityRepository.findAll().stream().map(a ->
                new ActivityDTO(
                        a.getId(),
                        a.getStartTime(),
                        a.getActivityType().toString(),
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
                        a.getRoute().stream().map(r ->
                                new LatLngDTO(r.getLatitude(), r.getLongitude())
                        ).collect(Collectors.toList()),
                        a.getTitle(),
                        a.isPublic())).collect(Collectors.toList());
    }

    public ActivityDTO createActivity(ActivityDTO activityDTO, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        Activity activity = activityMapper.mapToActivityEntity(activityDTO, user);

        System.out.println("Activity " + activity.toString());

        Activity createdActivity = activityRepository.save(activity);

        return activityMapper.mapToActivityDTO(createdActivity);
    }

}
