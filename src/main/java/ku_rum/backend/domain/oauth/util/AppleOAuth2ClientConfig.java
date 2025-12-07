package ku_rum.backend.domain.oauth.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
@ConditionalOnProperty(prefix = "apple", name = "enabled", havingValue = "true")
public class AppleOAuth2ClientConfig {

    private final OAuth2ClientProperties oAuth2ClientProperties;
    private final AppleClientSecretGenerator appleClientSecretGenerator;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();

        Map<String, OAuth2ClientProperties.Registration> regMap = oAuth2ClientProperties.getRegistration();
        Map<String, OAuth2ClientProperties.Provider> providerMap = oAuth2ClientProperties.getProvider();

        for (String registrationId : regMap.keySet()) {
            OAuth2ClientProperties.Registration reg = regMap.get(registrationId);
            OAuth2ClientProperties.Provider provider = providerMap.get(registrationId);

            ClientRegistration.Builder builder = ClientRegistration
                    .withRegistrationId(registrationId)
                    .clientId(reg.getClientId())
                    .clientSecret(reg.getClientSecret())
                    .clientAuthenticationMethod(
                            new ClientAuthenticationMethod(reg.getClientAuthenticationMethod())
                    )
                    .authorizationGrantType(
                            new AuthorizationGrantType(reg.getAuthorizationGrantType())
                    )
                    .redirectUri(reg.getRedirectUri())
                    .scope(reg.getScope())
                    .clientName(
                            reg.getClientName() != null ? reg.getClientName() : registrationId
                    );

            if (provider != null) {
                builder
                        .authorizationUri(provider.getAuthorizationUri())
                        .tokenUri(provider.getTokenUri())
                        .userInfoUri(provider.getUserInfoUri())
                        .userNameAttributeName(provider.getUserNameAttribute());
            }

            if ("apple".equals(registrationId)) {
                String clientSecret = appleClientSecretGenerator.generateClientSecret();
                builder.clientSecret(clientSecret);
            }

            registrations.add(builder.build());
        }

        return new InMemoryClientRegistrationRepository(registrations);
    }
}
