package ku_rum.backend.domain.auth.application;

import ku_rum.backend.domain.auth.dto.request.ReissueRequest;
import ku_rum.backend.global.utill.RedisUtil;
import ku_rum.backend.global.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TokenBlacklistServiceTest {

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private RedisUtil redisUtil;

    @Test
    @DisplayName("Redis에 저장된 리프레시 토큰과 요청 토큰이 일치하면 검증 성공")
    void validateRefreshTokenInRedis_success() {
        // given
        Long userId = 123L;
        String refreshToken = "valid-refresh-token";
        ReissueRequest reissueRequest = new ReissueRequest(refreshToken);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getUserId()).thenReturn(userId);
        when(redisUtil.getRedisData(String.valueOf(userId))).thenReturn(refreshToken);

        // when
        boolean isValid = tokenBlacklistService.validateRefreshTokenInRedis(reissueRequest, principal);

        // then
        assertTrue(isValid);
        verify(redisUtil, times(1)).deleteRedisData(String.valueOf(userId));
    }

    @Test
    @DisplayName("Redis에 저장된 리프레시 토큰과 요청 토큰이 다르면 검증 실패")
    void validateRefreshTokenInRedis_fail() {
        // given
        Long userId = 123L;
        String refreshToken = "valid-refresh-token";
        String differentToken = "different-refresh-token";
        ReissueRequest reissueRequest = new ReissueRequest(differentToken);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getUserId()).thenReturn(userId);
        when(redisUtil.getRedisData(String.valueOf(userId))).thenReturn(refreshToken);

        // when
        boolean isValid = tokenBlacklistService.validateRefreshTokenInRedis(reissueRequest, principal);

        // then
        assertFalse(isValid);
        verify(redisUtil, never()).deleteRedisData(anyString());
    }

    @Test
    @DisplayName("Redis에 저장된 리프레시 토큰이 없으면 검증 실패")
    void validateRefreshTokenInRedis_fail_no_token() {
        // given
        Long userId = 123L;
        ReissueRequest reissueRequest = new ReissueRequest("some-refresh-token");
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getUserId()).thenReturn(userId);
        when(redisUtil.getRedisData(String.valueOf(userId))).thenReturn(null);

        // when
        boolean isValid = tokenBlacklistService.validateRefreshTokenInRedis(reissueRequest, principal);

        // then
        assertFalse(isValid);
        verify(redisUtil, never()).deleteRedisData(anyString());
    }

    @Test
    @DisplayName("AccessToken을 블랙리스트에 추가하고, Redis에서 해당 유저 데이터 삭제")
    void setBlackListInRedis_success() {
        // given
        String token = "expired-access-token";
        long expiredTime = 3600000L; // 1시간 후 만료
        Long userId = 123L;

        // when
        tokenBlacklistService.setBlackListInRedis(token, expiredTime, userId);

        // then
        verify(redisUtil, times(1)).setBlackList(eq(token), eq("logout"), eq(Duration.ofMillis(expiredTime)));
        verify(redisUtil, times(1)).deleteRedisData(String.valueOf(userId));
    }
}