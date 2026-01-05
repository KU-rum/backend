package ku_rum.backend.domain.oauth.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
public class AppleAuthorizationRequestConfig {

    @Bean
    public OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        DefaultOAuth2AuthorizationRequestResolver delegate =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest req = delegate.resolve(request);
                return customizeIfApple(request, req);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest req = delegate.resolve(request, clientRegistrationId);
                return customizeIfAppleById(clientRegistrationId, req);
            }

            private OAuth2AuthorizationRequest customizeIfApple(HttpServletRequest request, OAuth2AuthorizationRequest req) {
                if (req == null) return null;
                // /oauth2/authorization/apple 로 들어오는 케이스
                if (request.getRequestURI() != null && request.getRequestURI().endsWith("/apple")) {
                    return OAuth2AuthorizationRequest.from(req)
                            .additionalParameters(p -> p.put("response_mode", "form_post"))
                            .build();
                }
                return req;
            }

            private OAuth2AuthorizationRequest customizeIfAppleById(String clientRegistrationId, OAuth2AuthorizationRequest req) {
                if (req == null) return null;
                if ("apple".equals(clientRegistrationId)) {
                    return OAuth2AuthorizationRequest.from(req)
                            .additionalParameters(p -> p.put("response_mode", "form_post"))
                            .build();
                }
                return req;
            }
        };
    }
}
