package ku_rum.backend.domain.bookmark.domain;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.bookmark.application.BookmarkService;
import ku_rum.backend.domain.bookmark.dto.request.CreateBookmarkRequest;
import ku_rum.backend.domain.bookmark.dto.response.CreateBookmarkResponse;
import ku_rum.backend.domain.bookmark.dto.response.GetBookmarkResponse;
import ku_rum.backend.domain.bookmark.presentation.BookmarkController;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(BookmarkController.class)
@ActiveProfiles("test")
class BookmarkControllerTest extends RestDocsTestSupport {

    @MockBean
    BookmarkService bookmarkService;

    @MockBean
    private ApiLogRepository apiLogRepository;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @DisplayName("공지사항 북마크를 생성한다")
    @Test
    void createBookmark() throws Exception {
        // given
        Long noticeId = 1L;
        Long bookmarkId = 10L;

        CreateBookmarkRequest request = new CreateBookmarkRequest(noticeId);
        CreateBookmarkResponse response = new CreateBookmarkResponse(bookmarkId);

        given(bookmarkService.createBookmark(any(), any(CreateBookmarkRequest.class)))
                .willReturn(response);

        // when
        mockMvc.perform(post("/api/v1/bookmark")
                        .header("Authorization", "Bearer test-access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "noticeId": 1
                                }
                                """))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookmarkId").value(bookmarkId))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("공지사항 북마크 API")
                                .description("공지사항을 북마크로 등록합니다.")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰입니다.")
                                )
                                .requestFields(
                                        fieldWithPath("noticeId").description("북마크할 공지사항의 ID입니다.")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data.bookmarkId").description("생성된 북마크의 ID")
                                )
                                .build()
                )));
    }

    @DisplayName("공지사항 북마크 목록을 조회한다")
    @Test
    void getBookmark() throws Exception {
        // given
        Long bookmarkId = 10L;
        Long noticeId = 1L;
        String noticeName = "공지사항 테스트";

        GetBookmarkResponse response = new GetBookmarkResponse(bookmarkId, noticeId, noticeName);

        given(bookmarkService.getBookmark(any()))
                .willReturn(List.of(response));

        // when
        mockMvc.perform(get("/api/v1/bookmark")
                        .header("Authorization", "Bearer test-access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].bookmarkId").value(bookmarkId))
                .andExpect(jsonPath("$.data[0].noticeId").value(noticeId))
                .andExpect(jsonPath("$.data[0].noticeName").value(noticeName))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("공지사항 북마크 API")
                                .description("사용자가 북마크한 공지사항 목록을 조회합니다.")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data[].bookmarkId").description("북마크 ID"),
                                        fieldWithPath("data[].noticeId").description("북마크된 공지사항 ID"),
                                        fieldWithPath("data[].noticeName").description("북마크된 공지사항 제목")
                                )
                                .build()
                )));
    }

    @DisplayName("공지사항 북마크를 삭제한다")
    @Test
    void deleteBookmark() throws Exception {
        // given
        Long bookmarkId = 10L;

        doNothing().when(bookmarkService).deleteBookmark(any(), eq(bookmarkId));

        // when
        mockMvc.perform(delete("/api/v1/bookmark/{bookmarkId}", bookmarkId)
                        .header("Authorization", "Bearer test-access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("공지사항 북마크 API")
                                .description("사용자가 특정 북마크를 삭제합니다.")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰")
                                )
                                .pathParameters(
                                        parameterWithName("bookmarkId").description("삭제할 북마크 ID")
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