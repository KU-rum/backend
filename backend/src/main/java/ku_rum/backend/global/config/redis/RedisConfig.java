package ku_rum.backend.global.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
  }

  @Bean
  public RedisTemplate<String, Integer> redisTemplate() {
    RedisTemplate<String, Integer> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }

  @Bean
  @Primary
  public RedisTemplate<String, String> urlRedisTemplate() {
    RedisTemplate<String, String> urlRedisTemplate = new RedisTemplate<>();
    urlRedisTemplate.setKeySerializer(new StringRedisSerializer());
    urlRedisTemplate.setValueSerializer(new StringRedisSerializer());
    urlRedisTemplate.setConnectionFactory(redisConnectionFactory());
    return urlRedisTemplate;
  }
}