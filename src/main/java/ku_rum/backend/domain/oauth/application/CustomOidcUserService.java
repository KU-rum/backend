package ku_rum.backend.domain.oauth.application;

import ku_rum.backend.domain.oauth.domain.OAuth2MemberInfo;
import ku_rum.backend.domain.oauth.domain.PreSignupPrincipal;
import ku_rum.backend.domain.oauth.domain.ProviderType;
import ku_rum.backend.domain.oauth.handler.OAuth2MemberInfoFactory;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.exception.oauth.OAuthProviderMissMatchException;
import ku_rum.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            // Apple은 OIDC claims에서 정보 획득
            ProviderType providerType = ProviderType.APPLE;

            OAuth2MemberInfo memberInfo =
                    OAuth2MemberInfoFactory.getOauth2MemberInfo(providerType, oidcUser.getClaims());

            Optional<User> userOptional = userRepository.findByOauthId(memberInfo.getId());

            if (userOptional.isPresent()) {
                User member = userOptional.get();
                if (providerType != member.getProviderType()) {
                    throw new OAuthProviderMissMatchException(
                            "이미 " + member.getProviderType() + "로 가입된 계정입니다. 해당 계정으로 로그인해주세요."
                    );
                }
                // CustomUserDetails가 OAuth2User를 기대한다면, 여기서도 동일하게 래핑
                // (필요 시 create 시그니처에 맞게 조정)
                return oidcUser;
            }

            // 미가입자: PreSignupPrincipal로 처리하려면 OidcUser가 아니라 OAuth2User 흐름이 필요하므로,
            // 프로젝트 정책에 맞게 (1) PreSignupPrincipal을 OAuth2User로 유지하거나
            // (2) 별도 SuccessHandler에서 oidcUser claims로 프리사인업 처리하도록 조정하세요.
            // 최소 변경을 위해서는 "기존 가입자 로그인"부터 먼저 통과시키는 것을 권장합니다.
            return oidcUser;

        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }
}
