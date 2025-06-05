package com.notifications.loggingservice.repository;

import com.notifications.loggingservice.entity.EventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, String> {
    
    List<EventLog> findTop50ByOrderByTimestampDesc();
    
    List<EventLog> findByEventTypeOrderByTimestampDesc(String eventType);
    
    List<EventLog> findByUserIdOrderByTimestampDesc(String userId);
    
    List<EventLog> findByLogLevelOrderByTimestampDesc(EventLog.LogLevel logLevel);
    
    Page<EventLog> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime start, 
        LocalDateTime end, 
        Pageable pageable
    );
    
    @Query("SELECT e.eventType, COUNT(e) FROM EventLog e GROUP BY e.eventType")
    List<Object[]> countEventsByType();
    
    @Query("SELECT COUNT(e) FROM EventLog e WHERE e.timestamp >= :since")
    Long countEventsSince(@Param("since") LocalDateTime since);
}
