package ku_rum.backend.domain.oauth.config;

import ku_rum.backend.domain.oauth.util.AppleClientSecretGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
@ConditionalOnProperty(prefix = "apple", name = "enabled", havingValue = "true")
@ConditionalOnBean(AppleClientSecretGenerator.class)
public class AppleTokenClientConfig {

    private final AppleClientSecretGenerator appleClientSecretGenerator;

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> appleAwareTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
        OAuth2AuthorizationCodeGrantRequestEntityConverter delegate =
                new OAuth2AuthorizationCodeGrantRequestEntityConverter();

        client.setRequestEntityConverter(req -> {
            RequestEntity<?> entity = delegate.convert(req);

            // apple 아니면 그대로
            if (!"apple".equals(req.getClientRegistration().getRegistrationId())) {
                return entity;
            }

            @SuppressWarnings("unchecked")
            MultiValueMap<String, String> form = (MultiValueMap<String, String>) entity.getBody();

            MultiValueMap<String, String> mutated = new LinkedMultiValueMap<>();
            if (form != null) mutated.addAll(form);

            // 토큰 요청 직전에 JWT client_secret 생성/주입
            mutated.set("client_secret", appleClientSecretGenerator.generateClientSecret());

            return new RequestEntity<>(mutated, entity.getHeaders(), entity.getMethod(), entity.getUrl());
        });

        return client;
    }
}
