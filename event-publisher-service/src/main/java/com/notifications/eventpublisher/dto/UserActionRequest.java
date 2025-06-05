package com.notifications.eventpublisher.dto;

import jakarta.validation.constraints.NotBlank;

public class UserActionRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Action type is required")
    private String actionType;
    
    @NotBlank(message = "Action details are required")
    private String actionDetails;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getActionDetails() { return actionDetails; }
    public void setActionDetails(String actionDetails) { this.actionDetails = actionDetails; }
}
