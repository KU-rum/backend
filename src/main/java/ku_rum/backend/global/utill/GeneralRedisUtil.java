package ku_rum.backend.global.utill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeneralRedisUtil {

    private final RedisTemplate<String, Integer> redisTemplate;

    public Integer getValue(String key) {
        log.info("레디스에서 키 조회");
        return redisTemplate.opsForValue().get(key);
    }

    public void setValue(String key, Integer value) {
        redisTemplate.opsForValue().set(key, value);
        log.debug("레디스에 키, 값 저장 완료");
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
        log.info("레디스에 키 삭제 완료");
    }
}

