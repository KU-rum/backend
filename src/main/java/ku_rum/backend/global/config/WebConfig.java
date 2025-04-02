package ku_rum.backend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ku_rum.backend.global.interceptor.LoggingInterceptor;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ApiLogRepository apiLogRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor(new ObjectMapper(), apiLogRepository))
                .order(1)
                .addPathPatterns("/**");
    }
}
