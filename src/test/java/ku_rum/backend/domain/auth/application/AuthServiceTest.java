package ku_rum.backend.domain.auth.application;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import ku_rum.backend.domain.auth.dto.request.LoginRequest;
import ku_rum.backend.domain.auth.dto.request.ReissueRequest;
import ku_rum.backend.domain.common.firebase.application.NotificationService;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.domain.user.dto.response.TokenResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import ku_rum.backend.global.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("잘못된 로그인 정보로 로그인 시 예외를 던진다.")
    void login_fail_invalid_credentials() {
        // given
        LoginRequest loginRequest = new LoginRequest("wrongUser", "wrongPassword");

        // 인증 단계에서 BadCredentialsException 발생시키기
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("유저에 대한 로그인 오류 발생"));

        // when & then
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        // 실제 예외 메시지와 예상 메시지가 일치하는지 확인
        assertEquals("[유저에 대한 로그인 오류 발생]", exception.getMessage());
    }

    @Test
    @DisplayName("로그아웃 시 토큰을 블랙리스트에 추가한다.")
    void logout_success() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        String accessToken = "valid-access-token";
        Long userId = 123L;
        long expirationTime = 3600000L;

        when(jwtTokenAuthenticationFilter.resolveToken(request)).thenReturn(accessToken);
        when(jwtTokenProvider.getUserId(accessToken)).thenReturn(userId);
        when(jwtTokenProvider.getExpiredTime(accessToken)).thenReturn(System.currentTimeMillis() + expirationTime);
        doNothing().when(notificationService).deleteToken(userId);

        // when
        authService.logout(request);

        // then
        verify(tokenBlacklistService, times(1))
                .setBlackListInRedis(eq(accessToken), anyLong(), eq(userId));
    }

    @Test
    @DisplayName("리프레시 토큰으로 새 토큰을 발급한다.")
    void reissue_success() {
        // given
        ReissueRequest reissueRequest = new ReissueRequest("valid-refresh-token");
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        TokenResponse expectedTokenResponse = TokenResponse.of("new-access-token", "new-refresh-token", 10480000, 20400000, false);

        when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.getAuthentication("valid-refresh-token")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenBlacklistService.validateRefreshTokenInRedis(eq(reissueRequest), any(CustomUserDetails.class)))
                .thenReturn(true);
        when(jwtTokenProvider.createToken(authentication)).thenReturn(expectedTokenResponse);

        // when
        TokenResponse actualResponse = authService.reissue(reissueRequest);

        // then
        assertNotNull(actualResponse);
        assertEquals("new-access-token", actualResponse.accessToken());
        assertEquals("new-refresh-token", actualResponse.refreshToken());
    }

    @Test
    @DisplayName("블랙리스트된 리프레시 토큰으로 재발급 시 예외를 던진다.")
    void reissue_fail_blacklisted_token() {
        // given
        ReissueRequest reissueRequest = new ReissueRequest("blacklisted-refresh-token");
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(jwtTokenProvider.validateToken("blacklisted-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.getAuthentication("blacklisted-refresh-token")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenBlacklistService.validateRefreshTokenInRedis(eq(reissueRequest), any(CustomUserDetails.class)))
                .thenReturn(false);

        // when & then
        assertThrows(JwtException.class, () -> authService.reissue(reissueRequest));
    }
}