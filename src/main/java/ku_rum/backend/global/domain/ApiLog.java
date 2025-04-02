package ku_rum.backend.global.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String requestIP;

    private String httpMethod;

    private String requestURI;

    private boolean accessTokenExist;

    private String requestBody;

    private LocalDateTime requestTime;

    @Builder
    public ApiLog(String requestIP, String httpMethod, String requestURI, boolean accessTokenExist, String requestBody) {
        this.requestIP = requestIP;
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.accessTokenExist = accessTokenExist;
        this.requestBody = requestBody;
        this.requestTime = LocalDateTime.now();
    }
}
