package com.sba.googleAuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/events")
public class GoogleCalendarController {

    private final GoogleCalendarEventService googleCalendarEventService;

    @Autowired
    public GoogleCalendarController(GoogleCalendarEventService googleCalendarEventService) {
        this.googleCalendarEventService = googleCalendarEventService;
    }

    @PostMapping("/create-meet")
    public String createEvent( @RequestParam String summary,
                              @RequestParam String description, @RequestParam Date startDate,
                              @RequestParam Date endDate) throws Exception {
        String meetLink = googleCalendarEventService.createGoogleMeetEvent(summary, description, startDate, endDate);
        return "Event created successfully with link: " + meetLink;
    }
}

