package com.notifications.notificationservice.service;

import com.notifications.events.NotificationTriggeredEvent;
import com.notifications.events.UserRegisteredEvent;
import com.notifications.notificationservice.entity.Notification;
import com.notifications.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private StreamBridge streamBridge;

    @Bean
    public Consumer<UserRegisteredEvent> handleUserRegistered() {
        return event -> {
            // Créer une notification de bienvenue
            String notificationId = UUID.randomUUID().toString();
            
            NotificationTriggeredEvent notificationEvent = new NotificationTriggeredEvent(
                notificationId,
                event.getUserId(),
                "EMAIL",
                "Bienvenue !",
                "Bienvenue " + event.getFirstName() + " ! Votre compte a été créé avec succès.",
                event.getEmail()
            );
            
            // Publier l'événement de notification
            streamBridge.send("notificationTriggered-out-0", notificationEvent);
        };
    }

    @Bean
    public Consumer<NotificationTriggeredEvent> handleNotificationTriggered() {
        return event -> {
            try {
                // Sauvegarder la notification
                Notification notification = new Notification();
                notification.setId(event.getNotificationId());
                notification.setUserId(event.getUserId());
                notification.setType(Notification.NotificationType.valueOf(event.getType()));
                notification.setTitle(event.getTitle());
                notification.setMessage(event.getMessage());
                notification.setRecipient(event.getRecipient());
                
                notificationRepository.save(notification);

                // Envoyer la notification selon le type
                switch (event.getType()) {
                    case "EMAIL":
                        sendEmailNotification(notification);
                        break;
                    case "SMS":
                        sendSmsNotification(notification);
                        break;
                    case "PUSH":
                        sendPushNotification(notification);
                        break;
                }
                
                // Mettre à jour le statut
                notification.setStatus(Notification.NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                
            } catch (Exception e) {
                // Marquer comme échoué
                Notification notification = notificationRepository.findById(event.getNotificationId())
                    .orElse(new Notification());
                notification.setStatus(Notification.NotificationStatus.FAILED);
                notificationRepository.save(notification);
            }
        };
    }

    private void sendEmailNotification(Notification notification) {
        emailService.sendEmail(
            notification.getRecipient(),
            notification.getTitle(),
            notification.getMessage()
        );
    }

    private void sendSmsNotification(Notification notification) {
        // Simulation d'envoi SMS
        System.out.println("SMS envoyé à " + notification.getRecipient() + ": " + notification.getMessage());
    }

    private void sendPushNotification(Notification notification) {
        // Simulation d'envoi Push
        System.out.println("Push notification envoyée à " + notification.getUserId() + ": " + notification.getMessage());
    }
}
