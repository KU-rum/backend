package ku_rum.backend.domain.oauth.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class PreSignupPrincipal implements OAuth2User {

    private final ProviderType providerType;
    private final String oauthId;
    private final String email;
    private final Map<String, Object> attributes;

    private PreSignupPrincipal(ProviderType providerType, String oauthId, String email, Map<String, Object> attributes) {
        this.providerType = providerType;
        this.oauthId = oauthId;
        this.email = email;
        this.attributes = attributes;
    }

    public static PreSignupPrincipal of(ProviderType providerType, OAuth2MemberInfo info, Map<String, Object> attrs) {
        return new PreSignupPrincipal(providerType, info.getId(), info.getEmail(), attrs);
    }

    @Override public Map<String, Object> getAttributes() { return attributes; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }
    @Override public String getName() { return oauthId; }
}
