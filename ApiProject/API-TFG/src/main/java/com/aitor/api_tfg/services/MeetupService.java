package com.aitor.api_tfg.services;

import com.aitor.api_tfg.model.db.LatLng;
import com.aitor.api_tfg.model.db.Meetup;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.model.dto.MeetupCreateDTO;
import com.aitor.api_tfg.repositories.MeetupRepository;
import com.aitor.api_tfg.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetupService {

    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;

    public Meetup createMeerup(String organizerName, MeetupCreateDTO meetup) {
        Optional<User> user = userRepository.findByUsername(organizerName);
        if (user.isPresent()) {
            Meetup meetupEntity = Meetup.builder()
                    .title(meetup.getTitle())
                    .description(meetup.getDescription())
                    .dateTime(meetup.getDateTime())
                    .location(meetup.getLocation())
                    .maxParticipants(meetup.getMaxParticipants())
                    .locationCoordinates(
                            new LatLng(
                                    meetup.getLocationCoordinates().getLatitude(),
                                    meetup.getLocationCoordinates().getLongitude()
                            )
                    )
                    .sportType(meetup.getSportType())
                    .organizerId(user.get())
                    .build();

            meetupRepository.save(meetupEntity);
        }

        return null;
    }
}
