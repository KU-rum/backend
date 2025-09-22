package ku_rum.backend.global.security;

import ku_rum.backend.domain.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@NoArgsConstructor
@ToString
public class CustomUserDetails implements UserDetails, OAuth2User {
    private Long userId;
    private String username;
    private String email;
    private Collection<? extends GrantedAuthority> roles;
    private String password;
    private Map<String, Object> attributes;
    private boolean firstLogin;

    private CustomUserDetails(Long userId, String username, Collection<? extends GrantedAuthority> role, String password, boolean firstLogin) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = role;
        this.password = password;
        this.firstLogin = firstLogin;
    }

    private CustomUserDetails(Long userId, String username, String email, Collection<? extends GrantedAuthority> roles, String password, boolean firstLogin) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.password = password;
        this.firstLogin = firstLogin;
    }

    public static CustomUserDetails of(Long id, String username, Collection<? extends GrantedAuthority> role, String password, boolean firstLogin) {
        return new CustomUserDetails(id, username, role, password, firstLogin);
    }

    public static CustomUserDetails of(Long id, String username, String email, Collection<? extends GrantedAuthority> roles, String password, boolean firstLogin) {
        return new CustomUserDetails(id, username, email, roles, password, firstLogin);
    }

    public static CustomUserDetails from(User user) {
        return new CustomUserDetails(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                user.getPassword(),
                user.isFirstLogin()
        );
    }

    public static CustomUserDetails create(User user, Map<String, Object> attributes) {
        CustomUserDetails from = from(user);
        from.changeAttributes(attributes);
        return from;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return username;
    }

    private void changeAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
