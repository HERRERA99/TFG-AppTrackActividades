package com.aitor.api_tfg.services;

import com.aitor.api_tfg.model.db.LatLng;
import com.aitor.api_tfg.model.db.Meetup;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.model.dto.MeetupCreateDTO;
import com.aitor.api_tfg.model.dto.MeetupResponseDTO;
import com.aitor.api_tfg.model.dto.UserSearchDTO;
import com.aitor.api_tfg.repositories.MeetupRepository;
import com.aitor.api_tfg.repositories.UserRepository;
import com.aitor.api_tfg.utils.GpxCalculationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetupService {

    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;

    public Meetup createMeerup(String organizerName, MeetupCreateDTO meetup, MultipartFile gpxFile) {
        Optional<User> user = userRepository.findByUsername(organizerName);
        if (user.isEmpty()) throw new IllegalArgumentException("Usuario no encontrado");

        List<LatLng> routePoints = new ArrayList<>();
        List<Double> elevations = new ArrayList<>();
        double totalDistance = 0;
        double elevationGain = 0;

        if (gpxFile != null && !gpxFile.isEmpty()) {
            try (InputStream is = gpxFile.getInputStream()) {
                Document doc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(is);
                doc.getDocumentElement().normalize();
                NodeList trkpts = doc.getElementsByTagName("trkpt");

                // Extraer puntos y alturas
                for (int i = 0; i < trkpts.getLength(); i++) {
                    Element trkpt = (Element) trkpts.item(i);
                    double lat = Double.parseDouble(trkpt.getAttribute("lat"));
                    double lon = Double.parseDouble(trkpt.getAttribute("lon"));
                    double ele = Double.parseDouble(trkpt.getElementsByTagName("ele").item(0).getTextContent());

                    routePoints.add(new LatLng(lat, lon));
                    elevations.add(ele);
                }

                // Calcular métricas usando GpxCalculationUtils
                totalDistance = GpxCalculationUtils.calculateTotalDistance(routePoints);
                elevationGain = GpxCalculationUtils.calculateElevationGain(elevations);

            } catch (Exception e) {
                return meetupRepository.save(Meetup.builder()
                        .title(meetup.getTitle())
                        .description(meetup.getDescription())
                        .dateTime(meetup.getDateTime())
                        .location(meetup.getLocation())
                        .maxParticipants(meetup.getMaxParticipants())
                        .locationCoordinates(meetup.getLocationCoordinates())
                        .sportType(meetup.getSportType())
                        .organizerId(user.get())
                        .build()
                );
            }
        }

        // Guardar en la entidad
        Meetup meetupEntity = Meetup.builder()
                .title(meetup.getTitle())
                .description(meetup.getDescription())
                .dateTime(meetup.getDateTime())
                .location(meetup.getLocation())
                .distance(totalDistance)
                .elevationGain(elevationGain)
                .maxParticipants(meetup.getMaxParticipants())
                .locationCoordinates(meetup.getLocationCoordinates())
                .sportType(meetup.getSportType())
                .organizerId(user.get())
                .route(routePoints)
                .build();

        return meetupRepository.save(meetupEntity);
    }

    public Optional<MeetupResponseDTO> getMeetupById(Long id) {
        return meetupRepository.findById(id)
                .map(this::convertToDto);
    }

    private MeetupResponseDTO convertToDto(Meetup meetup) {
        return MeetupResponseDTO.builder()
                .title(meetup.getTitle())
                .description(meetup.getDescription())
                .dateTime(meetup.getDateTime())
                .location(meetup.getLocation())
                .distance(meetup.getDistance())
                .elevationGain(meetup.getElevationGain())
                .maxParticipants(meetup.getMaxParticipants())
                .locationCoordinates(meetup.getLocationCoordinates())
                .sportType(meetup.getSportType())
                .organizerId(meetup.getOrganizerId().getId()) // Asume que organizerId es User
                .participants(convertParticipantsToDto(meetup.getParticipants()))
                .route(meetup.getRoute())
                .build();
    }

    private List<UserSearchDTO> convertParticipantsToDto(List<User> participants) {
        if (participants == null) {
            return List.of();
        }
        return participants.stream()
                .map(user -> UserSearchDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getFirstname() + " " + user.getLastname())
                        .image(user.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
