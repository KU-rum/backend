package ku_rum.backend.domain.search.presentation;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.search.application.RecentSearchService;
import ku_rum.backend.domain.search.domain.RecentSearch;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecentSearchController.class)
@ActiveProfiles("test")
class RecentSearchControllerTest extends RestDocsTestSupport {

    @MockBean
    private RecentSearchService recentSearchService;

    @MockBean
    private ApiLogRepository apiLogRepository;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;


    @Test
    @DisplayName("최근 검색어 목록을 조회한다")
    @WithMockUser
    void getRecentSearches() throws Exception {
        // given
        List<RecentSearch> response = List.of(
                mockRecentSearch(1L, 1L, "장학금"),
                mockRecentSearch(2L, 1L, "등록금")
        );

        given(recentSearchService.list(20)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/notices/searches/recent")
                        .param("limit", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("최근 검색어 API")
                                        .description("최근 검색어 목록 조회")
                                        .queryParameters(
                                                parameterWithName("limit")
                                                        .description("조회할 최근 검색어 개수 (기본값 20)")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("최근 검색어 ID"),
                                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("유저 ID"),
                                                fieldWithPath("data[].keyword").type(JsonFieldType.STRING).description("검색 키워드"),
                                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("생성 시각"),
                                                fieldWithPath("data[].updatedAt").type(JsonFieldType.STRING).description("최근 검색 시각")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("최근 검색어를 단건 삭제한다")
    @WithMockUser
    void deleteRecentSearch() throws Exception {
        doNothing().when(recentSearchService).delete(1L);

        mockMvc.perform(delete("/api/v1/notices/searches/recent/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("최근 검색어 API")
                                        .description("최근 검색어 단건 삭제")
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("최근 검색어 전체 삭제")
    @WithMockUser
    void deleteAllRecentSearches() throws Exception {
        doNothing().when(recentSearchService).deleteAll();

        mockMvc.perform(delete("/api/v1/notices/searches/recent/all")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("최근 검색어 API")
                                        .description("최근 검색어 전체 삭제")
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("최근 검색어를 저장한다")
    @WithMockUser
    void saveRecentSearch() throws Exception {
        // given
        doNothing().when(recentSearchService).save("장학금");

        // when & then
        mockMvc.perform(post("/api/v1/notices/searches/recent")
                        .queryParam("keyword", "장학금")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("최근 검색어 API")
                                        .description("최근 검색어 저장")
                                        .queryParameters(
                                                parameterWithName("keyword")
                                                        .description("저장할 검색 키워드")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .build()
                        )
                ));
    }

    private RecentSearch mockRecentSearch(Long id, Long userId, String keyword) {
        RecentSearch rs = new RecentSearch(userId, keyword, LocalDateTime.now());
        ReflectionTestUtils.setField(rs, "id", id);
        return rs;
    }
}
