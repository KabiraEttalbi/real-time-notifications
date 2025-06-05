package com.notifications.webinterface.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifications.events.NotificationTriggeredEvent;
import com.notifications.events.UserActionPerformedEvent;
import com.notifications.events.UserRegisteredEvent;
import com.notifications.webinterface.dto.NotificationDto;
import com.notifications.webinterface.dto.UserRegistrationDto;
import com.notifications.webinterface.entity.NotificationLog;
import com.notifications.webinterface.repository.NotificationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class WebInterfaceService {
    
    @Autowired
    private StreamBridge streamBridge;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private NotificationLogRepository logRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    public ResponseEntity<String> registerUser(UserRegistrationDto dto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<UserRegistrationDto> request = new HttpEntity<>(dto, headers);
            
            return restTemplate.postForEntity(
                "http://user-service:8081/api/users/register", 
                request, 
                String.class
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    public void sendNotification(NotificationDto dto) {
        String notificationId = UUID.randomUUID().toString();
        
        NotificationTriggeredEvent event = new NotificationTriggeredEvent(
            notificationId,
            dto.getUserId(),
            dto.getType(),
            dto.getTitle(),
            dto.getMessage(),
            dto.getRecipient()
        );
        
        streamBridge.send("notificationTriggered-out-0", event);
    }

    public void logUserAction(String userId, String actionType, String details) {
        UserActionPerformedEvent event = new UserActionPerformedEvent(
            userId, actionType, details
        );
        
        streamBridge.send("userActionPerformed-out-0", event);
    }

    public List<NotificationLog> getRecentLogs(int limit) {
        return logRepository.findTop10ByOrderByTimestampDesc();
    }

    @Bean
    public Consumer<UserRegisteredEvent> handleUserRegisteredForWeb() {
        return event -> {
            try {
                // Log l'événement
                NotificationLog log = new NotificationLog(
                    "USER_REGISTERED",
                    event.getUserId(),
                    objectMapper.writeValueAsString(event)
                );
                logRepository.save(log);
                
                // Envoyer via WebSocket
                messagingTemplate.convertAndSend("/topic/events", event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    public Consumer<NotificationTriggeredEvent> handleNotificationTriggeredForWeb() {
        return event -> {
            try {
                // Log l'événement
                NotificationLog log = new NotificationLog(
                    "NOTIFICATION_TRIGGERED",
                    event.getUserId(),
                    objectMapper.writeValueAsString(event)
                );
                logRepository.save(log);
                
                // Envoyer via WebSocket
                messagingTemplate.convertAndSend("/topic/notifications", event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    public Consumer<UserActionPerformedEvent> handleUserActionPerformedForWeb() {
        return event -> {
            try {
                // Log l'événement
                NotificationLog log = new NotificationLog(
                    "USER_ACTION_PERFORMED",
                    event.getUserId(),
                    objectMapper.writeValueAsString(event)
                );
                logRepository.save(log);
                
                // Envoyer via WebSocket
                messagingTemplate.convertAndSend("/topic/actions", event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
