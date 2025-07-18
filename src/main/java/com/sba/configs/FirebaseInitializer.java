package com.sba.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseInitializer {

    @Value("${firebase.config.path:}") // optional, nếu không có cũng không lỗi
    private Resource firebaseConfigPath;

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount;

            // 1️⃣ Ưu tiên dùng biến môi trường FIREBASE_CONFIG (Render)
            String firebaseEnv = System.getenv("FIREBASE_CONFIG");
            if (firebaseEnv != null && !firebaseEnv.isBlank()) {
                serviceAccount = new ByteArrayInputStream(firebaseEnv.getBytes(StandardCharsets.UTF_8));
            }
            // 2️⃣ Nếu không có thì fallback sang đọc file từ resources (local dev)
            else if (firebaseConfigPath != null && firebaseConfigPath.exists()) {
                serviceAccount = firebaseConfigPath.getInputStream();
            } else {
                throw new IllegalStateException("No Firebase config found in env or local file.");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        }

        return FirebaseAuth.getInstance();
    }
}
