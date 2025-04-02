package ku_rum.backend.global.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletResponse;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepositoryImpl;
import ku_rum.backend.global.batch.BatchScheduler;
import ku_rum.backend.global.domain.ApiLog;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import ku_rum.backend.global.interceptor.LoggingInterceptor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureMockMvc(addFilters = false)
class LoggingInterceptorTest {

    @InjectMocks
    private LoggingInterceptor loggingInterceptor;

    @Mock
    private ApiLogRepository apiLogRepository;

    @Mock
    private ObjectMapper objectMapper;

    @MockBean
    private NoticeRepositoryImpl noticeRepository;

    @MockBean
    private BatchScheduler batchScheduler;


    @Test
    void afterCompletion_정상적인_요청이면_로그가_저장된다() throws Exception {
        // Given
        ContentCachingRequestWrapper cachingRequest = mock(ContentCachingRequestWrapper.class); // 실제로 ContentCachingRequestWrapper를 mock
        HttpServletResponse response = mock(HttpServletResponse.class);
        Object handler = mock(Object.class);

        // Mock behavior for ContentCachingRequestWrapper
        given(cachingRequest.getMethod()).willReturn("POST");
        given(cachingRequest.getRequestURI()).willReturn("/api/v1/users");
        given(cachingRequest.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer token");
        given(cachingRequest.getHeader("X-Forwarded-For")).willReturn("192.168.1.1");

        byte[] content = "{\"test\":\"@konkuk.ac.kr\",\"email\":\"test123\",\"password\":\"test123\",\"studentId\":\"202112322\",\"department\":\"컴퓨터공학부\",\"nickname\":\"미미미누\"}".getBytes(StandardCharsets.UTF_8);
        given(cachingRequest.getContentAsByteArray()).willReturn(content);  // Mock the body content

        given(objectMapper.readTree(content)).willReturn(new ObjectNode(JsonNodeFactory.instance));

        // When
        loggingInterceptor.afterCompletion(cachingRequest, response, handler, null);

        // Then
        ArgumentCaptor<ApiLog> logCaptor = ArgumentCaptor.forClass(ApiLog.class);
        verify(apiLogRepository, times(1)).save(logCaptor.capture());

        ApiLog savedLog = logCaptor.getValue();
        assertThat(savedLog.getHttpMethod()).isEqualTo("POST");
        assertThat(savedLog.getRequestURI()).isEqualTo("/api/v1/users");
        assertThat(savedLog.isAccessTokenExist()).isTrue();
        assertThat(savedLog.getRequestIP()).isEqualTo("192.168.1.1");
    }

    @Test
    void afterCompletion_요청본문이_없어도_예외없이_동작한다() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/test/no-body");

        MockHttpServletResponse response = new MockHttpServletResponse();
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);

        when(objectMapper.readTree(any(byte[].class))).thenReturn(null);

        // When
        loggingInterceptor.afterCompletion(cachingRequest, response, null, null);

        // Then
        verify(apiLogRepository, times(1)).save(any(ApiLog.class));
    }
}