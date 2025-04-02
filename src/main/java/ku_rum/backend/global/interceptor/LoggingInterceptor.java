package ku_rum.backend.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ku_rum.backend.global.domain.ApiLog;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private final ApiLogRepository apiLogRepository;

    @Override
    public void afterCompletion(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Exception ex
    ) throws Exception {
        final ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;

        String requestIp = request.getHeader("X-Forwarded-For");
        if (requestIp == null) requestIp = request.getRemoteAddr();

        ApiLog apiLog = ApiLog.builder()
                .httpMethod(request.getMethod())
                .requestURI(request.getRequestURI())
                .accessTokenExist(StringUtils.hasText(request.getHeader(HttpHeaders.AUTHORIZATION)))
                .requestBody(String.valueOf(objectMapper.readTree(cachingRequest.getContentAsByteArray())))
                .requestIP(requestIp)
                .build();
        apiLogRepository.save(apiLog);

        log.info(
                "\n HTTP Method : {} " +
                        "\n Request URI : {} " +
                        "\n AccessToken Exist : {} " +
                        "\n Request Body : {}" +
                        "\n Request Time : {}" +
                        "\n Request IP : {}",
                apiLog.getHttpMethod(),
                apiLog.getRequestURI(),
                apiLog.isAccessTokenExist(),
                apiLog.getRequestBody(),
                apiLog.getRequestTime(),
                apiLog.getRequestIP()
        );
    }
}