package ku_rum.backend.global.utill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecentSearchRedisUtil {

    @Qualifier("recentSearchRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    public void saveSearchTerm(Long userId, String searchTerm) {
        String redisKey = "user:" + userId + ":recent-searches";
        redisTemplate.opsForList().leftPush(redisKey, searchTerm.trim());
        redisTemplate.opsForList().trim(redisKey, 0, 9);
    }

    public List<String> getRecentSearchTerms(Long userId) {
        String redisKey = "user:" + userId + ":recent-searches";
        List<String> terms = redisTemplate.opsForList().range(redisKey, 0, 9);
        return terms != null ? terms : List.of();
    }

    public void clearRecentSearches(Long userId) {
        redisTemplate.delete("user:" + userId + ":recent-searches");
    }
}
