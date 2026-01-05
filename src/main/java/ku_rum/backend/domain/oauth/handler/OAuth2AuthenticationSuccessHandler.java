package ku_rum.backend.domain.oauth.handler;

import com.github.dockerjava.api.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ku_rum.backend.domain.oauth.domain.OAuth2MemberInfo;
import ku_rum.backend.domain.oauth.domain.PreSignupPrincipal;
import ku_rum.backend.domain.oauth.domain.ProviderType;
import ku_rum.backend.domain.oauth.util.AppProperties;
import ku_rum.backend.domain.oauth.util.CookieUtils;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.exception.oauth.OAuthProviderMissMatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static ku_rum.backend.domain.oauth.handler.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AppProperties appProperties;
    private final TempTokenProvider tempTokenProvider;
    private final PreSignupTokenProvider preSignupTokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    // 추가: Apple도 여기서 가입/미가입 판단을 해야 하므로 필요
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        setDefaultTargetUrl("https://ku-room.vercel.app/oauth/callback");
        setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("응답이 이미 커밋되었습니다. " + targetUrl + "로 리다이렉트 할 수 없습니다");
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("승인되지 않은 리다이렉션 URI입니다");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        // Apple은 OIDC principal 형태가 달라서 여기서 별도 처리 (PreSignup 플로우 유지)
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            String registrationId = oauth2Token.getAuthorizedClientRegistrationId();

            if ("apple".equals(registrationId)) {
                OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

                // Apple은 sub가 고유 식별자
                OAuth2MemberInfo memberInfo = OAuth2MemberInfoFactory.getOauth2MemberInfo(
                        ProviderType.APPLE,
                        oidcUser.getClaims()
                );

                Optional<User> userOptional = userRepository.findByOauthId(memberInfo.getId());

                if (userOptional.isPresent()) {
                    User member = userOptional.get();

                    if (ProviderType.APPLE != member.getProviderType()) {
                        throw new OAuthProviderMissMatchException(
                                "이미 " + member.getProviderType() + "로 가입된 계정입니다. 해당 계정으로 로그인해주세요."
                        );
                    }

                    // 기존 가입자: userId 기반으로 temp token 발급
                    String tempToken = tempTokenProvider.createTempTokenByUserId(member.getId());

                    return UriComponentsBuilder.fromUriString(targetUrl)
                            .queryParam("needSignup", false)
                            .queryParam("token", tempToken)
                            .build().toUriString();
                }

                // 미가입자: 기존과 동일하게 PreSignupPrincipal 기반 토큰 발급
                PreSignupPrincipal pre = PreSignupPrincipal.of(
                        ProviderType.APPLE, memberInfo, oidcUser.getClaims()
                );
                String preToken = preSignupTokenProvider.create(pre);

                return UriComponentsBuilder.fromUriString(targetUrl)
                        .queryParam("needSignup", true)
                        .queryParam("token", preToken)
                        .build().toUriString();
            }
        }

        // ---- 기존 로직 (Google/Naver/Kakao 등) 그대로 유지 ----
        Object principal = authentication.getPrincipal();

        if (principal instanceof PreSignupPrincipal pre) {
            String preToken = preSignupTokenProvider.create(pre);
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("needSignup", true)
                    .queryParam("token", preToken)
                    .build().toUriString();
        }

        String tempToken = tempTokenProvider.createTempToken(authentication);
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("needSignup", false)
                .queryParam("token", tempToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
