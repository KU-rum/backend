package ku_rum.backend.domain.oauth.handler;

import io.jsonwebtoken.JwtException;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.utill.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TempTokenProvider {

    private final RedisUtil redisUtil;

    private static final long TEMP_TOKEN_EXPIRY_MILLIS = 5 * 60 * 1000; // 5분

    public String createTempToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = String.valueOf(userDetails.getUserId());

        String tempToken = UUID.randomUUID().toString();
        redisUtil.setRedisData("TEMP_TOKEN:" + tempToken, userId, TEMP_TOKEN_EXPIRY_MILLIS);
        return tempToken;
    }

    public String createTempTokenByUserId(Long userId) {
        String tempToken = UUID.randomUUID().toString();
        redisUtil.setRedisData("TEMP_TOKEN:" + tempToken, String.valueOf(userId), TEMP_TOKEN_EXPIRY_MILLIS);
        return tempToken;
    }

    public Long resolveUserId(String tempToken) {
        String userId = redisUtil.getRedisData("TEMP_TOKEN:" + tempToken);
        if (userId == null) {
            throw new JwtException("유효하지 않은 임시 토큰입니다");
        }
        return Long.valueOf(userId);
    }

    public void invalidateTempToken(String tempToken) {
        redisUtil.deleteRedisData("TEMP_TOKEN:" + tempToken);
    }
}