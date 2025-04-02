package ku_rum.backend.domain.common.firebase.application;

import ku_rum.backend.global.utill.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static ku_rum.backend.global.utill.UserUtil.getLongMemberId;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RedisUtil redisUtil;
    private static final String TOKEN_PREFIX = "notification:token:";

    public void register(final String token) {
        Long memberId = getLongMemberId();
        redisUtil.setRedisData(TOKEN_PREFIX + memberId, token);
        log.info("사용자 푸시 토큰 저장 완료");
    }

    public void deleteToken(Long userId) {
        redisUtil.deleteRedisData(TOKEN_PREFIX + userId);
        log.info("사용자 푸시 토큰 삭제 완료");
    }
}
