package com.aitor.api_tfg.services;

import com.aitor.api_tfg.model.db.LatLng;
import com.aitor.api_tfg.model.db.Meetup;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.model.dto.*;
import com.aitor.api_tfg.repositories.MeetupRepository;
import com.aitor.api_tfg.repositories.UserRepository;
import com.aitor.api_tfg.utils.GpxCalculationUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetupService {

    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;
    private final NotificacionesService notificacionesService;

    public MeetupResponseDTO createMeerup(String organizerName, MeetupCreateDTO meetup, MultipartFile gpxFile) {
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

                if (trkpts.getLength() == 0) {
                    throw new IllegalArgumentException("El archivo GPX no contiene puntos de ruta válidos");
                }

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
                throw new IllegalArgumentException("Error al procesar el archivo GPX: " + e.getMessage());
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
                .locationCoordinates(meetup.getLocationCoordinates())
                .sportType(meetup.getSportType())
                .organizerId(user.get())
                .participants(new ArrayList<>())
                .route(routePoints)
                .build();

        meetupEntity.addParticipant(user.get());

        meetupRepository.save(meetupEntity);

        return convertToDto(meetupEntity, user.get());
    }

    public MeetupResponseDTO getMeetupById(Long id, User user) {
        Meetup meetup = meetupRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Meetup not found with ID: " + id));
        return convertToDto(meetup, user);
    }

    private MeetupResponseDTO convertToDto(Meetup meetup, User currentUser) {
        boolean isParticipating = meetup.getParticipants().contains(currentUser);

        return MeetupResponseDTO.builder()
                .id(meetup.getId())
                .title(meetup.getTitle())
                .description(meetup.getDescription())
                .dateTime(meetup.getDateTime())
                .location(meetup.getLocation())
                .distance(meetup.getDistance())
                .elevationGain(meetup.getElevationGain())
                .locationCoordinates(meetup.getLocationCoordinates())
                .sportType(meetup.getSportType())
                .organizerId(meetup.getOrganizerId().getId())
                .participants(convertParticipantsToDto(meetup.getParticipants()))
                .route(meetup.getRoute())
                .isParticipating(isParticipating)
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

    public Page<Meetup> getMeetupsOrderedByDistance(double lat, double lng, Pageable pageable) {
        return meetupRepository.findMeetupsOrderedByDistance(lat, lng, pageable);
    }

    public Page<Meetup> getUserMeetupsOrderedByDistance(Integer userId, Pageable pageable) {
        return meetupRepository.findMeetupsOrganizedByUserOrderedByDate(userId, pageable);
    }

    public MeetupResponseDTO joinMeetup(Long meetupId, User user) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Meetup not found with ID: " + meetupId));

        if (meetup.getParticipants().contains(user)) {
            throw new IllegalStateException("User is already joined to this meetup.");
        }

        meetup.addParticipant(user);

        Meetup updatedMeetup = meetupRepository.save(meetup);

        // Notificar al organizador
        User organizer = meetup.getOrganizerId();
        if (organizer != null && organizer.getFcmToken() != null && !organizer.getId().equals(user.getId())) {
            notificacionesService.sendPushNotification(
                    organizer.getFcmToken(),
                    "¡Nuevo participante!",
                    user.getFirstname() + " se ha unido a tu quedada \"" + meetup.getTitle() + "\""
            );
        }

        return convertToDto(updatedMeetup, user);
    }


    public MeetupResponseDTO leaveMeetup(Long meetupId, User user) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Meetup not found with ID: " + meetupId));

        if (!meetup.getParticipants().contains(user)) {
            throw new IllegalStateException("User is not joined to this meetup.");
        }

        meetup.removeParticipant(user);

        Meetup updatedMeetup = meetupRepository.save(meetup);

        return convertToDto(updatedMeetup, user);
    }

    public MeetupDeleteDTO deleteMeetup(Long meetupId, User user) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new EntityNotFoundException("Meetup not found with ID: " + meetupId));

        if (!meetup.getOrganizerId().getId().equals(user.getId())) {
            throw new IllegalStateException("User is not the organizer.");
        }

        // Notificar a todos los participantes antes de eliminar
        List<User> participants = meetup.getParticipants();
        for (User participant : participants) {
            if (participant.getFcmToken() != null && !participant.getFcmToken().isBlank()
                    && !participant.getId().equals(user.getId())) {

                notificacionesService.sendPushNotification(
                        participant.getFcmToken(),
                        "Quedada cancelada",
                        "La quedada \"" + meetup.getTitle() + "\" ha sido cancelada por el organizador."
                );
            }
        }

        meetupRepository.deleteById(meetupId);

        return MeetupDeleteDTO.builder()
                .meetupId(meetupId)
                .organizerId(user.getId())
                .dateTime(LocalDateTime.now())
                .build();
    }

}
