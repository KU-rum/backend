package ku_rum.backend.domain.oauth.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "apple")
public class AppleProperties {

    private String teamId;
    private String keyId;
    private String clientId;
    private String keyPath;
    private long tokenValidSeconds = 60 * 60 * 24 * 180; // default 180일
}
