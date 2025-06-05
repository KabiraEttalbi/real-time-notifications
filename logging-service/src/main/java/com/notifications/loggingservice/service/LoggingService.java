package com.notifications.loggingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifications.events.NotificationTriggeredEvent;
import com.notifications.events.UserActionPerformedEvent;
import com.notifications.events.UserRegisteredEvent;
import com.notifications.loggingservice.entity.EventLog;
import com.notifications.loggingservice.repository.EventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class LoggingService {
    
    @Autowired
    private EventLogRepository eventLogRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public Consumer<UserRegisteredEvent> logUserRegistered() {
        return event -> {
            try {
                EventLog log = new EventLog(
                    "USER_REGISTERED",
                    "user-service",
                    objectMapper.writeValueAsString(event),
                    event.getUserId()
                );
                log.setAdditionalInfo("New user registration: " + event.getEmail());
                eventLogRepository.save(log);
                
                System.out.println("✅ Logged USER_REGISTERED event for user: " + event.getEmail());
            } catch (Exception e) {
                System.err.println("❌ Error logging USER_REGISTERED event: " + e.getMessage());
            }
        };
    }

    @Bean
    public Consumer<NotificationTriggeredEvent> logNotificationTriggered() {
        return event -> {
            try {
                EventLog log = new EventLog(
                    "NOTIFICATION_TRIGGERED",
                    "notification-service",
                    objectMapper.writeValueAsString(event),
                    event.getUserId()
                );
                log.setAdditionalInfo("Notification sent: " + event.getType() + " to " + event.getRecipient());
                eventLogRepository.save(log);
                
                System.out.println("✅ Logged NOTIFICATION_TRIGGERED event: " + event.getType());
            } catch (Exception e) {
                System.err.println("❌ Error logging NOTIFICATION_TRIGGERED event: " + e.getMessage());
            }
        };
    }

    @Bean
    public Consumer<UserActionPerformedEvent> logUserActionPerformed() {
        return event -> {
            try {
                EventLog log = new EventLog(
                    "USER_ACTION_PERFORMED",
                    "event-publisher-service",
                    objectMapper.writeValueAsString(event),
                    event.getUserId()
                );
                log.setAdditionalInfo("User action: " + event.getActionType());
                eventLogRepository.save(log);
                
                System.out.println("✅ Logged USER_ACTION_PERFORMED event: " + event.getActionType());
            } catch (Exception e) {
                System.err.println("❌ Error logging USER_ACTION_PERFORMED event: " + e.getMessage());
            }
        };
    }

    public List<EventLog> getRecentLogs(int limit) {
        return eventLogRepository.findTop50ByOrderByTimestampDesc()
            .stream()
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<EventLog> getLogsByEventType(String eventType) {
        return eventLogRepository.findByEventTypeOrderByTimestampDesc(eventType);
    }

    public List<EventLog> getLogsByUserId(String userId) {
        return eventLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public Page<EventLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end, pageable);
    }

    public Map<String, Long> getEventStatistics() {
        List<Object[]> results = eventLogRepository.countEventsByType();
        return results.stream()
            .collect(Collectors.toMap(
                result -> (String) result[0],
                result -> (Long) result[1]
            ));
    }

    public Long getEventCountSince(LocalDateTime since) {
        return eventLogRepository.countEventsSince(since);
    }

    public void logCustomEvent(String eventType, String source, String data, String userId, String additionalInfo) {
        EventLog log = new EventLog(eventType, source, data, userId);
        log.setAdditionalInfo(additionalInfo);
        eventLogRepository.save(log);
    }
}
