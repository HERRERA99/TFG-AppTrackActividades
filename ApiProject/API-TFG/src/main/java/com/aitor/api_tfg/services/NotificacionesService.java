package com.aitor.api_tfg.services;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


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
