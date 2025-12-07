package ku_rum.backend.domain.oauth.domain;

import java.util.Map;

public class AppleOAuth2MemberInfo extends OAuth2MemberInfo {

    public AppleOAuth2MemberInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub"); // Apple unique ID
    }

    @Override
    public String getName() {
        // Apple은 name을 보내지 않는 경우가 많음
        return (String) attributes.getOrDefault("name", "Apple User");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        // Apple은 프로필 이미지 제공 X
        return null;
    }
}
