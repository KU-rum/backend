package ku_rum.backend.domain.notice.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.notice.application.SearchKeywordService;
import ku_rum.backend.domain.notice.dto.request.SaveKeywordRequest;
import ku_rum.backend.domain.notice.dto.response.GetKeywordResponse;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(KeywordController.class)
@ActiveProfiles("test")
public class KeywordControllerTest extends RestDocsTestSupport {

    @MockBean
    private SearchKeywordService searchKeywordService;

    @MockBean
    private ApiLogRepository apiLogRepository;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @DisplayName("키워드 검색")
    @Test
    void getKeyword() throws Exception {
        //given
        String keyword = "키워드";
        GetKeywordResponse response = new GetKeywordResponse(List.of(keyword));

        given(searchKeywordService.getKeyword(any()))
                .willReturn(response);
        //when
        mockMvc.perform(get("/api/v1/notices/keyword")
                        .header("Authorization", "Bearer test-access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("공지사항 키워드 API")
                                .description("사용자가 저장한 키워드 리스트를 조회합니다")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data.keywords").description("키워드 리스트")
                                )
                                .build()
                )));
    }


    @DisplayName("키워드 저장 또는 삭제")
    @Test
    void updateKeyword() throws Exception {
        //given
        String keyword = "키워드";
        SaveKeywordRequest request = new SaveKeywordRequest(keyword);

        doNothing().when(searchKeywordService).update(eq(request), any());

        //when
        mockMvc.perform(post("/api/v1/notices/keyword")
                        .header("Authorization", "Bearer test-access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "keyword": "키워드"
                                }
                                """))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("공지사항 키워드 API")
                                .description("사용자가 저장한 키워드 저장 또는 삭제합니다(새로운 키워드 -> 저장, 기존 키워드 -> 삭제")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지")
                                )
                                .build()
                )));
    }
}
