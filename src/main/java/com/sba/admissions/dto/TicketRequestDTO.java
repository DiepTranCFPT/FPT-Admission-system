package com.sba.admissions.dto;

import java.time.LocalDateTime;

public class TicketRequestDTO {
    private String staffId;
    private LocalDateTime createAt;
    private String topic;
    private String content;
    private String response;
    private String status;
    private String userId;

    public TicketRequestDTO() {}

    public TicketRequestDTO(String staffId, LocalDateTime createAt, String topic, String content, String response, String status, String userId) {
        this.staffId = staffId;
        this.createAt = createAt;
        this.topic = topic;
        this.content = content;
        this.response = response;
        this.status = status;
        this.userId = userId;
    }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public LocalDateTime getCreateAt() { return createAt; }
    public void setCreateAt(LocalDateTime createAt) { this.createAt = createAt; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
