package ku_rum.backend.domain.notice.domain;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.notice.application.NoticeService;
import ku_rum.backend.domain.notice.dto.response.RecentSearchTermResponse;
import ku_rum.backend.global.batch.BatchConfig;
import ku_rum.backend.global.log.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.security.JwtTokenProvider;
import ku_rum.backend.global.utils.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@EnableScheduling
class NoticeRecentControllerTest  extends RestDocsTestSupport {

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
    private ApiLogRepository apiLogRepository;

    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        customUserDetails = Mockito.mock(CustomUserDetails.class);
        given(customUserDetails.getUserId()).willReturn(1L);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities()));
        SecurityContextHolder.setContext(securityContext);
    }

    @DisplayName("최근 검색어 목록 조회 테스트")
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void searchTerms() throws Exception {
        RecentSearchTermResponse mockResponse = new RecentSearchTermResponse(1L, List.of("검색어1", "검색어2"));
        given(noticeService.getRecentSearchTerms(any())).willReturn(mockResponse);

        mockMvc.perform(get("/api/v1/notices/recent")
                        .with(user(customUserDetails))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.searchedList[0]").value("검색어1"))
                .andExpect(jsonPath("$.data.searchedList[1]").value("검색어2"))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("공지사항 API")
                                        .description("최근 검색 단어 불러오기")
                                        .responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("요청 성공 여부"),
                                            fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                            fieldWithPath("data.searchedList").type(JsonFieldType.ARRAY).description("최근 검색어 목록")
                                        ).build())));
    }
}