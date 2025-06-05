package com.notifications.webinterface.repository;

import com.notifications.webinterface.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {
    List<NotificationLog> findTop10ByOrderByTimestampDesc();
    List<NotificationLog> findByEventTypeOrderByTimestampDesc(String eventType);
    List<NotificationLog> findByUserIdOrderByTimestampDesc(String userId);
}
