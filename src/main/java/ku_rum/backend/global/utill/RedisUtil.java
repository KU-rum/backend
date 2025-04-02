package ku_rum.backend.global.utill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public String getRedisData(String key){
        log.info("레디스에서 키 조회");
        return redisTemplate.opsForValue().get(key);
    }

    public void setRedisData(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        log.debug("레디스에 키, 값 저장 완료");
    }

    public void deleteRedisData(String key) {
        redisTemplate.delete(key);
        log.info("레디스에 키 삭제 완료");
    }

    public void setBlackList(String key, String o, Duration minutes) {
        redisTemplate.opsForValue().set(key, o, minutes);
        log.info("레디스에 블랙리스트 저장 완료");

    }

    public String getBlackList(String key) {
        if (redisTemplate.opsForValue().get(key) == null) {
            log.error("레디스 블랙리스트에 키가 존재하지 않습니다.");
            return "false";
        }
        return redisTemplate.opsForValue().get(key);
    }
}
