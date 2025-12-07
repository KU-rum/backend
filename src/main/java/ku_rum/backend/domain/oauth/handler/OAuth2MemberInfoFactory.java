package ku_rum.backend.domain.oauth.handler;


import ku_rum.backend.domain.oauth.domain.*;

import java.util.Map;

public class OAuth2MemberInfoFactory {
    public static OAuth2MemberInfo getOauth2MemberInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case GOOGLE:
                return new GoogleOAuth2MemberInfo(attributes);
            case NAVER:
                return new NaverOAuth2MemberInfo(attributes);
            case KAKAO:
                return new KakaoOAuth2MemberInfo(attributes);
            case APPLE:
                return new AppleOAuth2MemberInfo(attributes);
            default:
                throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}