package com.notifications.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class UserActionPerformedEvent {
    private String userId;
    private String actionType;
    private String actionDetails;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public UserActionPerformedEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public UserActionPerformedEvent(String userId, String actionType, String actionDetails) {
        this();
        this.userId = userId;
        this.actionType = actionType;
        this.actionDetails = actionDetails;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getActionDetails() { return actionDetails; }
    public void setActionDetails(String actionDetails) { this.actionDetails = actionDetails; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "UserActionPerformedEvent{" +
                "userId='" + userId + '\'' +
                ", actionType='" + actionType + '\'' +
                ", actionDetails='" + actionDetails + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
