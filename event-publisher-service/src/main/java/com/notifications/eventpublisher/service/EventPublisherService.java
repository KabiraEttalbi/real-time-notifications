package com.notifications.eventpublisher.service;

import com.notifications.events.NotificationTriggeredEvent;
import com.notifications.events.UserActionPerformedEvent;
import com.notifications.eventpublisher.dto.NotificationRequest;
import com.notifications.eventpublisher.dto.UserActionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventPublisherService {
    
    @Autowired
    private StreamBridge streamBridge;

    public void publishNotificationEvent(NotificationRequest request) {
        String notificationId = UUID.randomUUID().toString();
        
        NotificationTriggeredEvent event = new NotificationTriggeredEvent(
            notificationId,
            request.getUserId(),
            request.getType(),
            request.getTitle(),
            request.getMessage(),
            request.getRecipient()
        );
        
        streamBridge.send("notificationTriggered-out-0", event);
        System.out.println("📤 Published NotificationTriggered event: " + notificationId);
    }

    public void publishUserActionEvent(UserActionRequest request) {
        UserActionPerformedEvent event = new UserActionPerformedEvent(
            request.getUserId(),
            request.getActionType(),
            request.getActionDetails()
        );
        
        streamBridge.send("userActionPerformed-out-0", event);
        System.out.println("📤 Published UserActionPerformed event: " + request.getActionType());
    }
}
