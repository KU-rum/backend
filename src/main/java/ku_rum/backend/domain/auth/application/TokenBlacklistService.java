package ku_rum.backend.domain.auth.application;

import ku_rum.backend.domain.auth.dto.request.ReissueRequest;
import ku_rum.backend.global.utill.RedisUtil;
import ku_rum.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class TokenBlacklistService {
    private final RedisUtil redisUtil;

    public boolean validateRefreshTokenInRedis(ReissueRequest reissueRequest, CustomUserDetails principal) {
        Long userId = principal.getUserId();
        String redisRefreshToken = redisUtil.getRedisData(String.valueOf(userId));

        if (redisRefreshToken != null && redisRefreshToken.equals(reissueRequest.refreshToken())) {
            redisUtil.deleteRedisData(String.valueOf(userId));
            log.info("레디스에 리프레시 토큰을 재생성 하였습니다.");
            return true;
        }
        log.debug("레디스에 해당 리프레시 토큰이 존재하지 않습니다.");
        return false;
    }

    public void setBlackListInRedis(String token, long expiredAccessTokenTime, Long userId) {
        redisUtil.setBlackList(token, "logout", Duration.ofMillis(expiredAccessTokenTime));
        redisUtil.deleteRedisData(String.valueOf(userId));
        log.info("사용자가 로그아웃 완료 했습니다.");
    }
}
