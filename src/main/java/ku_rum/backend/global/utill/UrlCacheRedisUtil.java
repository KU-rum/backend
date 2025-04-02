package ku_rum.backend.global.utill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRedisUtil {

    @Qualifier("urlRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    private static final String NOTICE_REDIS_KEY_PREFIX = "konkuk:notice:";

    public boolean isUrlCached(String url) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(NOTICE_REDIS_KEY_PREFIX + url));
    }

    public void cacheUrl(String url) {
        redisTemplate.opsForValue().set(NOTICE_REDIS_KEY_PREFIX + url, url);
    }

    public void deleteCachedUrl(String url) {
        redisTemplate.delete(NOTICE_REDIS_KEY_PREFIX + url);
    }
}
