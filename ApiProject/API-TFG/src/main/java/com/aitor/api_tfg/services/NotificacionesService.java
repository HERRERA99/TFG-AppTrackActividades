package com.aitor.api_tfg.services;

import com.aitor.api_tfg.model.db.Meetup;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.repositories.MeetupRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionesService {
    private final MeetupRepository meetupRepository;

    public void sendPushNotification(String fcmToken, String title, String body) {
        if (fcmToken == null || fcmToken.isBlank()) return;

        Message message = Message.builder()
                .setToken(fcmToken)
                .putData("title", title)
                .putData("body", body)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void notificarProximasQuedadas() {
        OffsetDateTime ahora = OffsetDateTime.now();
        OffsetDateTime dentroDeUnaHora = ahora.plusHours(1);

        List<Meetup> proximasQuedadas = meetupRepository
                .findByDateTimeBetween(ahora.plusMinutes(59), dentroDeUnaHora.plusMinutes(1)); // tolerancia

        for (Meetup meetup : proximasQuedadas) {
            for (User participante : meetup.getParticipants()) {
                if (participante.getFcmToken() != null && !participante.getFcmToken().isBlank()) {
                    sendPushNotification(
                            participante.getFcmToken(),
                            "¡Tu quedada empieza pronto!",
                            "La quedada \"" + meetup.getTitle() + "\" comienza en 1 hora."
                    );
                }
            }
        }
    }
}
