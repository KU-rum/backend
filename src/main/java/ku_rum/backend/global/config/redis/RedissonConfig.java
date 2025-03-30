package ku_rum.backend.global.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient(@Value("${spring.data.redis.host}") String host,
                                         @Value("${spring.data.redis.port}") String port) {

        Config config = new Config();
        config.useSingleServer()
                .setAddress(REDISSON_HOST_PREFIX + host + ":" + port)
                .setDatabase(1);

        return Redisson.create(config);
    }
}
