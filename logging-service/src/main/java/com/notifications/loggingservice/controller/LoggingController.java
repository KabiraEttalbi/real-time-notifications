package com.notifications.loggingservice.controller;

import com.notifications.loggingservice.entity.EventLog;
import com.notifications.loggingservice.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LoggingController {
    
    @Autowired
    private LoggingService loggingService;

    @GetMapping
    public ResponseEntity<List<EventLog>> getRecentLogs(
            @RequestParam(defaultValue = "50") int limit) {
        List<EventLog> logs = loggingService.getRecentLogs(limit);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<List<EventLog>> getLogsByType(@PathVariable String eventType) {
        List<EventLog> logs = loggingService.getLogsByEventType(eventType);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventLog>> getLogsByUser(@PathVariable String userId) {
        List<EventLog> logs = loggingService.getLogsByUserId(userId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/range")
    public ResponseEntity<Page<EventLog>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<EventLog> logs = loggingService.getLogsByDateRange(start, end, page, size);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getEventStatistics() {
        Map<String, Long> stats = loggingService.getEventStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/count/since")
    public ResponseEntity<Long> getEventCountSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        Long count = loggingService.getEventCountSince(since);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/custom")
    public ResponseEntity<String> logCustomEvent(
            @RequestParam String eventType,
            @RequestParam String source,
            @RequestParam String data,
            @RequestParam String userId,
            @RequestParam(required = false) String additionalInfo) {
        
        loggingService.logCustomEvent(eventType, source, data, userId, additionalInfo);
        return ResponseEntity.ok("Event logged successfully");
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "logging-service",
            "timestamp", LocalDateTime.now(),
            "totalLogs", loggingService.getEventCountSince(LocalDateTime.now().minusDays(30))
        );
        return ResponseEntity.ok(health);
    }
}
