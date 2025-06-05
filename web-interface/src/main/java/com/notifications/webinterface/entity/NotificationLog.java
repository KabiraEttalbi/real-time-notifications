package com.notifications.webinterface.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String eventType;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false, length = 2000)
    private String eventData;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public NotificationLog() {
        this.timestamp = LocalDateTime.now();
    }

    public NotificationLog(String eventType, String userId, String eventData) {
        this();
        this.eventType = eventType;
        this.userId = userId;
        this.eventData = eventData;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
