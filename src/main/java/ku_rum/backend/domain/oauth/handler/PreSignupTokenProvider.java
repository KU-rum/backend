package ku_rum.backend.domain.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import ku_rum.backend.domain.oauth.domain.PreSignupPrincipal;
import ku_rum.backend.domain.oauth.domain.ProviderType;
import ku_rum.backend.global.utill.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PreSignupTokenProvider {

    private final RedisUtil redisUtil;
    private static final long PRE_SIGNUP_EXPIRY_MILLIS = 30 * 60 * 1000; // 30분
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String create(PreSignupPrincipal pre) {
        String token = UUID.randomUUID().toString();
        PreSignupPayload payload = new PreSignupPayload(
                pre.getProviderType(),
                pre.getOauthId(),
                pre.getEmail(),
                pre.getAttributes()
        );
        redisUtil.setRedisData(key(token), toJson(payload), PRE_SIGNUP_EXPIRY_MILLIS);
        return token;
    }

    public PreSignupPayload resolve(String token) {
        String json = redisUtil.getRedisData(key(token));
        if (json == null) {
            throw new JwtException("유효하지 않은 프리사인업 토큰입니다");
        }
        return fromJson(json);
    }

    public void invalidate(String token) {
        redisUtil.deleteRedisData(key(token));
    }

    private String key(String token) {
        return "PRE_SIGNUP:" + token;
    }

    private String toJson(PreSignupPayload payload) {
        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private PreSignupPayload fromJson(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, PreSignupPayload.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PreSignupPayload {
        private ProviderType providerType;
        private String oauthId;
        private String email; // nullable
        private Map<String, Object> attributes; // 원시 속성 (필요시 사용할 수 있음)
    }
}
