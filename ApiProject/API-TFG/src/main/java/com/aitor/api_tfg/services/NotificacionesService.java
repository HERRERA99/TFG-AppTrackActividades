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
    public void sendPushNotification(String fcmToken, String title, String body) {
        if (fcmToken == null || fcmToken.isBlank()) return;

        Message message = Message.builder()
                .setToken(fcmToken)
                .putData("title", title)
                .putData("body", body)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}
