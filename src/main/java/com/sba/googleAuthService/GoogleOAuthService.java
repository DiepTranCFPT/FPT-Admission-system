package com.sba.googleAuthService;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleOAuthService {

    @Value("${credentialsFile}")
    private String credentialsFile;

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
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        String redirectUri = "http://localhost:5173/staff/admissionschedule";
        String authUrl = flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setAccessType("offline")
                .build();

        System.out.println("Google OAuth Authorization URL: " + authUrl);

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(5173).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public String getGoogleOAuthAuthorizationUrl() throws Exception {
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        String redirectUri = "http://localhost:5173/staff/admissionschedule";
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setAccessType("offline")
                .build();
    }

    public Credential exchangeCodeForCredential(String code) throws Exception {
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        String redirectUri = "http://localhost:5173/staff/admissionschedule";
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        return flow.createAndStoreCredential(tokenResponse, "e19e433c-c2b6-403e-8078-7fd0c8aef56b");
    }

    private GoogleClientSecrets loadClientSecrets() throws Exception {
        if (credentialsFile == null || credentialsFile.isBlank()) {
            throw new IllegalStateException("Missing 'credentialsfile' environment variable.");
        }

        ByteArrayInputStream stream = new ByteArrayInputStream(credentialsFile.getBytes(StandardCharsets.UTF_8));
        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(stream));
    }
}
