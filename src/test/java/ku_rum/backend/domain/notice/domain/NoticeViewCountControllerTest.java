package ku_rum.backend.domain.notice.domain;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.BackendApplication;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.notice.application.NoticeService;
import ku_rum.backend.domain.notice.application.ViewCountService;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.global.batch.BatchConfig;
import ku_rum.backend.global.log.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.security.JwtTokenProvider;
import ku_rum.backend.global.utils.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackendApplication.class)
@ActiveProfiles("test")
public class NoticeViewCountControllerTest extends RestDocsTestSupport {

    @MockBean
    private ViewCountService viewCountService;

    @MockBean
    private NoticeService noticeService;

    @MockBean
    private BatchConfig batchConfig;

    @MockBean
    private JobLauncher jobLauncher;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private ApiLogRepository apiLogRepository;

    private CustomUserDetails customUserDetails;

    @DisplayName("공지사항 조회수 증가 테스트")
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void increaseViewCount() throws Exception {
        // Given
        String url = "https://www.konkuk.ac.kr/bbs/konkuk/234/1147020/artclView.do";
        String responseMessage = url + "에 대해 조회수가 증가되었습니다.";
        doNothing().when(viewCountService).enqueueLockRequest(anyString());

        // When & Then
        mockMvc.perform(get("/api/v1/notices/url")
                        .param("url", url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(responseMessage))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("공지사항 API")
                                .description("공지사항 조회수 증가")
                                .queryParameters(
                                        parameterWithName("url").description("조회수를 증가시킬 공지사항 URL")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드 (200)"),
                                        fieldWithPath("status").description("응답 상태 (OK)"),
                                        fieldWithPath("message").description("응답 메시지 (OK)"),
                                        fieldWithPath("data").description("조회수 증가 결과 메시지")
                                )
                                .build()
                )));
    }

    @Test
    @DisplayName("조회수가 가장 많은 공지사항 목록 조회 테스트")
    @WithMockUser(username = "testUser", roles = "USER")
    void testMostViewedNotices() throws Exception {
        // Given
        when(viewCountService.mostViewedNotices()).thenReturn(List.of("2025학년도 1학기 분할납부 2차 납부안내(고지서 출력)"));

        // When & Then
        mockMvc.perform(get("/api/v1/notices/popular/url")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data[0]").value("2025학년도 1학기 분할납부 2차 납부안내(고지서 출력)"))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("공지사항 API")
                                .description("조회수가 가장 많은 공지사항 목록 조회")
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드 (200)"),
                                        fieldWithPath("status").description("응답 상태 (OK)"),
                                        fieldWithPath("message").description("응답 메시지 (OK)"),
                                        fieldWithPath("data").description("조회수가 많은 공지사항 목록")
                                )
                                .build()
                )));
    }

}
