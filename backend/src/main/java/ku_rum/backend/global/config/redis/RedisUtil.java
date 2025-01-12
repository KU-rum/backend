package ku_rum.backend.global.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public String getRedisData(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void setRedisData(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void deleteRedisData(String key) {
        redisTemplate.delete(key);
    }

    public void setBlackList(String key, String o, Duration minutes) {
        redisTemplate.opsForValue().set(key, o, minutes);
    }

    public String getBlackList(String key) {
        if (redisTemplate.opsForValue().get(key) == null) {
            return "false";
        }
        return redisTemplate.opsForValue().get(key);
    }
}
