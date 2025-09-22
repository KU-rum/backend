package ku_rum.backend.domain.oauth.application;

import ku_rum.backend.domain.oauth.domain.OAuth2MemberInfo;
import ku_rum.backend.domain.oauth.handler.OAuth2MemberInfoFactory;
import ku_rum.backend.domain.oauth.domain.ProviderType;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.exception.oauth.OAuthProviderMissMatchException;
import ku_rum.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        try {
            return this.process(userRequest, user);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2MemberInfo memberInfo = OAuth2MemberInfoFactory.getOauth2MemberInfo(providerType, user.getAttributes());
        Optional<User> userOptional = userRepository.findByOauthId(memberInfo.getId());

        User member;
        if (userOptional.isPresent()) {
            member = userOptional.get();
            if (providerType != member.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                                " account. Please use your " + member.getProviderType() + " account to login."
                );
            }
            if (member.isFirstLogin()) {
                member.changeFirstLogin(false);
                userRepository.save(member);
            }
        } else {
            member = createUser(memberInfo, providerType);
        }
        return CustomUserDetails.create(member, user.getAttributes());
    }

    private User createUser(OAuth2MemberInfo memberInfo, ProviderType providerType) {
        User user = User.createMemberWithOAuthInfo(memberInfo, providerType);
        return userRepository.save(user);
    }
}