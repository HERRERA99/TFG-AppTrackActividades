package com.aitor.api_tfg.mappers;

import com.aitor.api_tfg.model.dto.ActivityDTO;
import com.aitor.api_tfg.model.dto.LatLngDTO;
import com.aitor.api_tfg.model.db.Activity;
import com.aitor.api_tfg.model.db.LatLng;
import com.aitor.api_tfg.model.db.Modalidades;
import com.aitor.api_tfg.model.db.Publication;
import com.aitor.api_tfg.model.dto.PublicationDTO;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.model.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActivityMapper {

    private Activity activityBuilder(ActivityDTO activityDTO, User user) {
        return Activity.builder()
                .id(activityDTO.getId())
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
                .distances(activityDTO.getDistances())
                .title(activityDTO.getTitle())
                .isPublic(activityDTO.isPublic())
                .user(user)
                .build();
    }

    public Activity mapToActivityEntity(ActivityDTO activityDTO, User user) {
        return activityBuilder(activityDTO, user);
    }

    public List<LatLng> convertLatLngDTOs(List<LatLngDTO> dtos) {
        return dtos.stream()
                .map(dto -> new LatLng(dto.getLatitude(), dto.getLongitude()))
                .collect(Collectors.toList());
    }

    public List<LatLngDTO> convertLatLngs(List<LatLng> latLngs) {
        return latLngs.stream()
                .map(l -> new LatLngDTO(l.getLatitude(), l.getLongitude()))
                .collect(Collectors.toList());
    }

    public ActivityDTO mapToActivityDTO(Activity activity) {
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
                .distances(activity.getDistances())
                .title(activity.getTitle())
                .isPublic(activity.isPublic())
                .build();
    }

    public Publication mapToPublicationEntity(Activity activity, User user) {
        return Publication.builder()
                .user(user)
                .activity(activity)
                .creationDate(activity.getStartTime())
                .isPublic(activity.isPublic())
                .build();
    }

    public PublicationDTO mapToPublicationDTO(Publication publication) {
        return PublicationDTO.builder()
                .id(publication.getId())
                .user(new UserDTO(publication.getUser().getUsername(), publication.getUser().getImageUrl()))
                .title(publication.getActivity().getTitle())
                .startTime(publication.getActivity().getStartTime())
                .endTime(publication.getActivity().getEndTime())
                .activityType(publication.getActivity().getActivityType())
                .distance(publication.getActivity().getDistance())
                .duration(publication.getActivity().getDuration())
                .positiveElevation(publication.getActivity().getPositiveElevation())
                .averageSpeed(publication.getActivity().getAverageSpeed())
                .calories(publication.getActivity().getCalories())
                .maxSpeed(publication.getActivity().getMaxSpeed())
                .speeds(publication.getActivity().getSpeeds())
                .elevations(publication.getActivity().getElevations())
                .route(publication.getActivity().getRoute())
                .distances(publication.getActivity().getDistances())
                .maxAltitude(publication.getActivity().getMaxAltitude())
                .likes(publication.getLikes().stream().map(User::getId).collect(Collectors.toList()))
                .build();
    }
}
