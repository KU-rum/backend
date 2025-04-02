package ku_rum.backend.global.security.jwt;

import io.jsonwebtoken.JwtException;
import ku_rum.backend.global.utill.RedisUtil;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.security.JwtProperties;
import ku_rum.backend.global.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private JwtProperties jwtProperties;

    private final String testSecret = "test-secret-key-1234567890-1234567890";
    private final Long accessValidity = 3600000L; // 1 hour
    private final Long refreshValidity = 2592000000L; // 30 days

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret(testSecret);
        jwtProperties.setAccessTokenValiditySeconds(accessValidity);
        jwtProperties.setRefreshTokenValiditySeconds(refreshValidity);

        jwtTokenProvider = new JwtTokenProvider(jwtProperties, redisUtil);
        jwtTokenProvider.init();
    }

    // 1. 토큰 생성 테스트
    @Test
    void createToken_ValidAuthentication_ReturnsTokens() {
        // Given
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        Collection<GrantedAuthority> authorities = Collections.singleton(authority);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                CustomUserDetails.of(1L, "test", "test",
                        authorities,
                        "test"
                ), null, authorities);

        // When
        var tokenResponse = jwtTokenProvider.createToken(authentication);

        // Then
        assertThat(tokenResponse.accessToken()).isNotEmpty();
        assertThat(tokenResponse.refreshToken()).isNotEmpty();
        assertThat(tokenResponse.accessExpireIn()).isEqualTo(accessValidity);
        verify(redisUtil).setRedisData(eq("1"), anyString());
    }

    // 2. 유효한 토큰 인증 정보 추출 테스트
    @Test
    void getAuthentication_ValidToken_ReturnsAuthentication() {
        // Given
        String token = createTestToken();

        // When
        Authentication auth = jwtTokenProvider.getAuthentication(token);

        // Then
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        assertThat(principal.getUserId()).isEqualTo(1L);
        assertThat(auth.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    // 3. 토큰 유효성 검증 테스트
    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Given
        String validToken = createTestToken();

        // When & Then
        assertThat(jwtTokenProvider.validateToken(validToken)).isTrue();
    }

    @Test
    void validateToken_ExpiredToken_ThrowsException() {
        // Given
        jwtProperties.setAccessTokenValiditySeconds(0L); // 즉시 만료
        jwtTokenProvider.init();
        String expiredToken = createTestToken();

        // When & Then
        assertThatThrownBy(() -> jwtTokenProvider.validateToken(expiredToken))
                .isInstanceOf(JwtException.class);
    }

    // 4. 사용자 ID 추출 테스트
    @Test
    void getUserId_ValidToken_ReturnsUserId() {
        // Given
        String token = createTestToken();

        // When
        Long userId = jwtTokenProvider.getUserId(token);

        // Then
        assertThat(userId).isEqualTo(1L);
    }

    // 5. Redis 저장 실패 테스트
    @Test
    void setRedisData_Failure_ThrowsException() {
        // Given
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        Collection<GrantedAuthority> authorities = Collections.singleton(authority);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                CustomUserDetails.of(1L, "test", "test",
                        authorities,
                        "test"
                ), null, authorities);

        doThrow(new RuntimeException("Redis error")).when(redisUtil)
                .setRedisData(anyString(), anyString());

        // When & Then
        assertThatThrownBy(() -> jwtTokenProvider.createToken(authentication))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("레디스에 리프레시 토큰 저장 실패");
    }

    private String createTestToken() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        Collection<GrantedAuthority> authorities = Collections.singleton(authority);

        CustomUserDetails userDetails = CustomUserDetails.of(
                1L,
                "test",
                "test",
                authorities,
                "test"
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authorities
        );
        return jwtTokenProvider.createToken(authentication).accessToken();
    }
}