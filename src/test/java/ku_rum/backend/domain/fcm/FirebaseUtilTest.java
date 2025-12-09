package ku_rum.backend.domain.fcm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class FirebaseUtilTest {

    @Test
    @DisplayName("FCM 설정 파일의 존재 유무를 확인한다")
    void serviceAccountFile_shouldExist() throws Exception {
        Resource resource = new ClassPathResource("config/firebase-service.json");
        assertTrue(resource.exists());
    }
}