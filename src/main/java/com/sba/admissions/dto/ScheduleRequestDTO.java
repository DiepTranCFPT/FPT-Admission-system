package com.sba.admissions.dto;

import java.time.LocalDateTime;

public class ScheduleRequestDTO {
    private String staffId;
    private LocalDateTime admissionAt;
    private String status;

    private String meetLink;

    public ScheduleRequestDTO() {}

    public ScheduleRequestDTO(String staffId, LocalDateTime createAt, LocalDateTime admissionAt, String status, String userId, String meetLink) {
        this.staffId = staffId;
        this.admissionAt = admissionAt;
        this.status = status;
        this.meetLink = meetLink;
    }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public LocalDateTime getAdmissionAt() { return admissionAt; }
    public void setAdmissionAt(LocalDateTime admissionAt) { this.admissionAt = admissionAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMeetLink() { return meetLink; }
    public void setMeetLink(String meetLink) { this.meetLink = meetLink; }
}
