package ku_rum.backend.domain.oauth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Profile("!test")
@ConditionalOnProperty(prefix = "apple", name = "enabled", havingValue = "true")
public class AppleClientSecretGenerator {

    private final AppleProperties appleProperties;

    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        this.privateKey = AppleKeyUtils.loadPrivateKey(appleProperties.getKeyPath());
    }

    public String generateClientSecret() {
        long now = System.currentTimeMillis();
        long exp = now + appleProperties.getTokenValidSeconds() * 1000L;

        Map<String, Object> header = new HashMap<>();
        header.put("kid", appleProperties.getKeyId());
        header.put("alg", "ES256");

        return Jwts.builder()
                .setHeader(header)
                .setIssuer(appleProperties.getTeamId())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .setAudience("https://appleid.apple.com")
                .setSubject(appleProperties.getClientId())
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact();
    }
}
