package com.sba.admissions.controller;

import com.sba.admissions.dto.ScheduleRequestDTO;
import com.sba.admissions.dto.ScheduleResponseDTO;
import com.sba.admissions.pojos.AdmissionSchedules;
import com.sba.admissions.service.ScheduleService;
import com.sba.googleAuthService.GoogleCalendarEventService;
import com.sba.googleAuthService.GoogleOAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@Transactional
public class ScheduleController {

    private final ScheduleService scheduleService;

    private final GoogleCalendarEventService googleCalendarEventService;

    private final GoogleOAuthService googleOAuthService;

    public ScheduleController(ScheduleService scheduleService, GoogleCalendarEventService googleCalendarEventService, GoogleOAuthService googleOAuthService) {
        this.scheduleService = scheduleService;
        this.googleCalendarEventService = googleCalendarEventService;
        this.googleOAuthService = googleOAuthService;
    }

    @PostMapping
    public ResponseEntity<ScheduleResponseDTO> createSchedule(@RequestBody LocalDateTime request) {
        return ResponseEntity.ok(scheduleService.createSchedule(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdmissionSchedules> getScheduleById(@PathVariable String id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @GetMapping
    public ResponseEntity<List<AdmissionSchedules>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> updateSchedule(@PathVariable String id, @RequestBody ScheduleRequestDTO request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/google-auth-url")
    public ResponseEntity<String> getGoogleAuthUrl() throws Exception {
        String url = googleOAuthService.getGoogleOAuthAuthorizationUrl();
        return ResponseEntity.ok(url);
    }

    @PutMapping("/meeting-link/{id}")
    public ResponseEntity<ScheduleResponseDTO> createMeetingLink(
            @PathVariable String id,
            @RequestParam("code") String code) throws Exception {
        String summary = "Tư vấn tuyển sinh";
        String description = "Cuộc hẹn tư vấn tuyển sinh giữa staff và user";
        Date startDate = java.sql.Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Date endDate = new Date(startDate.getTime() + 30 * 60 * 1000);
        String googleMeetLink = googleCalendarEventService.createGoogleMeetEventWithCode(
            summary, description, startDate, endDate, code);
        return ResponseEntity.ok(scheduleService.responseStaff(googleMeetLink, id));
    }

    @GetMapping("/oauth2/callback")
    public ResponseEntity<String> googleOAuthCallback(@RequestParam(required = false) String code, @RequestParam(required = false) String error) {
        if (error != null) {
            return ResponseEntity.badRequest().body("Google OAuth error: " + error);
        }
        if (code == null) {
            return ResponseEntity.badRequest().body("Missing code parameter from Google OAuth");
        }
        return ResponseEntity.ok("Google OAuth code: " + code);
    }
}