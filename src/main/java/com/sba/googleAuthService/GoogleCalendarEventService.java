package com.sba.googleAuthService;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
public class GoogleCalendarEventService {

    private final GoogleOAuthService googleOAuthService;

    public GoogleCalendarEventService(GoogleOAuthService googleOAuthService) {
        this.googleOAuthService = googleOAuthService;
    }

    public String createGoogleMeetEvent(String summary, String description, Date startDate, Date endDate) throws Exception {
        Calendar calendarService = googleOAuthService.getGoogleCalendarService();
        Event event = new Event()
                .setSummary(summary)
                .setDescription(description)
                .setStart(new EventDateTime().setDateTime(new DateTime(startDate)).setTimeZone("Asia/Ho_Chi_Minh"))
                .setEnd(new EventDateTime().setDateTime(new DateTime(endDate)).setTimeZone("Asia/Ho_Chi_Minh"))
                .setConferenceData(new ConferenceData()
                        .setCreateRequest(new CreateConferenceRequest().setRequestId("random-request-id").setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"))));
        Event createdEvent = calendarService.events().insert("primary", event).setConferenceDataVersion(1).execute();
        return createdEvent.getHangoutLink(); // Đây là link Google Meet được tạo
    }

    /**
     * Tạo Google Meet event bằng mã code OAuth vừa xác thực
     */
    public String createGoogleMeetEventWithCode( String code) throws Exception {
        // 1. Đổi code lấy access token
        Credential credential = googleOAuthService.exchangeCodeForCredential(code);
        Calendar calendarService = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Spring Boot Google Calendar Example")
                .build();
        // 2. Tạo event Google Calendar có Google Meet
        Event event = new Event()
                .setSummary("summary")
                .setDescription("Cuộc hẹn tư vấn tuyển sinh giữa staff và user")
                .setStart(new EventDateTime().setDateTime(new DateTime(java.sql.Timestamp.valueOf(LocalDate.now().atStartOfDay()))).setTimeZone("Asia/Ho_Chi_Minh"))
                .setEnd(new EventDateTime().setDateTime(new DateTime(new Date(java.sql.Timestamp.valueOf(LocalDate.now().atStartOfDay()).getTime() + 30 * 60 * 1000))).setTimeZone("Asia/Ho_Chi_Minh"))
                .setConferenceData(new ConferenceData()
                        .setCreateRequest(new CreateConferenceRequest().setRequestId("random-" + System.currentTimeMillis()).setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"))));
        Event createdEvent = calendarService.events().insert("primary", event).setConferenceDataVersion(1).execute();
        return createdEvent.getHangoutLink();
    }
}
