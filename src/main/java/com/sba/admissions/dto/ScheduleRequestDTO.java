package com.sba.admissions.dto;

import java.time.LocalDateTime;

public class ScheduleRequestDTO {
    private String staffId;
    private LocalDateTime createAt;
    private LocalDateTime admissionAt;
    private String status;
    private String userId;
    private String meetLink;

    public ScheduleRequestDTO() {}

    public ScheduleRequestDTO(String staffId, LocalDateTime createAt, LocalDateTime admissionAt, String status, String userId, String meetLink) {
        this.staffId = staffId;
        this.createAt = createAt;
        this.admissionAt = admissionAt;
        this.status = status;
        this.userId = userId;
        this.meetLink = meetLink;
    }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public LocalDateTime getCreateAt() { return createAt; }
    public void setCreateAt(LocalDateTime createAt) { this.createAt = createAt; }

    public LocalDateTime getAdmissionAt() { return admissionAt; }
    public void setAdmissionAt(LocalDateTime admissionAt) { this.admissionAt = admissionAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMeetLink() { return meetLink; }
    public void setMeetLink(String meetLink) { this.meetLink = meetLink; }
}
