package ku_rum.backend.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() {

        try {
            Resource resource = new ClassPathResource("config/firebase-service.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();

            if (!FirebaseApp.getApps().isEmpty()) {
                throw new IllegalStateException("FCM 이미 설정 완료");
            }

            FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
            return FirebaseMessaging.getInstance(firebaseApp);

        } catch (IOException e) {
            throw new IllegalStateException("FCM 설정 실패", e);
        }
    }
}