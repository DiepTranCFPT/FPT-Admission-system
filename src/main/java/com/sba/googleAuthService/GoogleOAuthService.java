package com.sba.googleAuthService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleOAuthService {

    @Value("${credentialsfile}")
    private String credentialsfile;

    private static final String APPLICATION_NAME = "Spring Boot Google Calendar Example";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

    public Calendar getGoogleCalendarService() throws Exception {
        Credential credential = authorize();
        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential authorize() throws Exception {
        InputStream in = new FileInputStream(credentialsfile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singletonList("https://www.googleapis.com/auth/calendar"))
                .setAccessType("offline")
                .build();


        //url authorize staff
        String redirectUri = "http://localhost:5173/staff/admissionschedule";
        String authUrl = flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setAccessType("offline")
                .build();
        System.out.println("Google OAuth Authorization URL: " + authUrl);

        // Authorize user (this will prompt for consent if needed)
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(5173).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return credential;
    }

    public String getGoogleOAuthAuthorizationUrl() throws Exception {
        InputStream in = new FileInputStream(credentialsfile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singletonList("https://www.googleapis.com/auth/calendar"))
                .setAccessType("offline")
                .build();

        String redirectUri = "http://localhost:5173/staff/admissionschedule";
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setAccessType("offline")
                .build();
    }

    /**
     * Đổi mã code lấy Credential (access token) cho staff vừa xác thực Google OAuth
     */
    public Credential exchangeCodeForCredential(String code) throws Exception {
        InputStream in = new FileInputStream(credentialsfile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singletonList("https://www.googleapis.com/auth/calendar"))
                .setAccessType("offline")
                .build();
        String redirectUri = "http://localhost:5173/staff/admissionschedule";
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
        return flow.createAndStoreCredential(tokenResponse, "e19e433c-c2b6-403e-8078-7fd0c8aef56b");
    }
}
