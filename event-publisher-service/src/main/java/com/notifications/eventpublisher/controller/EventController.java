package com.notifications.eventpublisher.controller;

import com.notifications.eventpublisher.dto.NotificationRequest;
import com.notifications.eventpublisher.dto.UserActionRequest;
import com.notifications.eventpublisher.service.EventPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    
    @Autowired
    private EventPublisherService eventPublisherService;

    @PostMapping("/trigger-notification")
    public ResponseEntity<Map<String, String>> triggerNotification(
            @Valid @RequestBody NotificationRequest request) {
        try {
            eventPublisherService.publishNotificationEvent(request);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Notification event published successfully",
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Failed to publish notification event: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/user-action")
    public ResponseEntity<Map<String, String>> logUserAction(
            @Valid @RequestBody UserActionRequest request) {
        try {
            eventPublisherService.publishUserActionEvent(request);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User action event published successfully",
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Failed to publish user action event: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "event-publisher-service",
            "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.ok(health);
    }
}
