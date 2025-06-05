package com.notifications.loggingservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_logs")
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String eventType;
    
    @Column(nullable = false)
    private String eventSource;
    
    @Column(nullable = false, length = 2000)
    private String eventData;
    
    @Column(nullable = false)
    private String userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel logLevel;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(length = 500)
    private String additionalInfo;

    public EventLog() {
        this.timestamp = LocalDateTime.now();
        this.logLevel = LogLevel.INFO;
    }

    public EventLog(String eventType, String eventSource, String eventData, String userId) {
        this();
        this.eventType = eventType;
        this.eventSource = eventSource;
        this.eventData = eventData;
        this.userId = userId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEventSource() { return eventSource; }
    public void setEventSource(String eventSource) { this.eventSource = eventSource; }

    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LogLevel getLogLevel() { return logLevel; }
    public void setLogLevel(LogLevel logLevel) { this.logLevel = logLevel; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}
