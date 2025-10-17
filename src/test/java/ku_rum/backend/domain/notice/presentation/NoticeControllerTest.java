package ku_rum.backend.domain.notice.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.notice.application.NoticeService;
import ku_rum.backend.domain.notice.dto.response.NoticeDetailResponse;
import ku_rum.backend.domain.notice.dto.response.NoticeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class NoticeControllerTest extends RestDocsTestSupport {

    @MockBean
    private NoticeService noticeService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @DisplayName("카테고리 ID로 공지사항 목록을 조회한다.")
    @Test
    void getNoticesByCategory() throws Exception {
        //given
        Long categoryId = Long.valueOf(234);
        Pageable pageable = PageRequest.of(0, 2);

        List<NoticeResponse> noticeList = List.of(
                new NoticeResponse(1L, categoryId, "학사", "공지 제목 1", "https://link1.com", "2024-01-01", "관리자", "설명1"),
                new NoticeResponse(2L, categoryId, "학사", "공지 제목 2", "https://link2.com", "2024-01-02", "관리자", "설명2")
        );

        Page<NoticeResponse> page = new PageImpl<>(noticeList, pageable, noticeList.size());

        given(noticeService.findByCategory(eq(categoryId), any(Pageable.class)))
                .willReturn(page);

        //when & then
        mockMvc.perform(get("/api/v1/notices")
                        .param("category", categoryId.toString())
                        .param("page", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("공지 제목 1"))
                .andExpect(jsonPath("$.content[1].title").value("공지 제목 2"))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("공지사항 관련 API")
                                .description("카테고리 ID로 공지사항 목록 조회")
                                .queryParameters(
                                        parameterWithName("category").description("공지사항 카테고리 ID"),
                                        parameterWithName("page").optional().description("페이지 번호"),
                                        parameterWithName("size").optional().description("페이지 크기")
                                )
                                .responseFields(
                                        fieldWithPath("content[].id").description("공지사항 ID"),
                                        fieldWithPath("content[].categoryId").description("카테고리 ID"),
                                        fieldWithPath("content[].categoryName").description("카테고리 이름"),
                                        fieldWithPath("content[].title").description("공지 제목"),
                                        fieldWithPath("content[].link").description("공지 링크"),
                                        fieldWithPath("content[].pubDate").description("게시일"),
                                        fieldWithPath("content[].author").description("작성자"),
                                        fieldWithPath("content[].description").description("공지 설명"),
                                        subsectionWithPath("pageable").ignored(),
                                        fieldWithPath("totalPages").description("전체 페이지 수"),
                                        fieldWithPath("totalElements").description("전체 요소 수"),
                                        fieldWithPath("last").description("마지막 페이지 여부"),
                                        fieldWithPath("size").description("페이지 크기"),
                                        fieldWithPath("number").description("현재 페이지 번호"),
                                        subsectionWithPath("sort").ignored(),
                                        fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                        fieldWithPath("first").description("첫 페이지 여부"),
                                        fieldWithPath("empty").description("비어 있는지 여부")
                                )
                                .build())));
    }

    @DisplayName("공지사항 ID로 공지 상세 HTML을 조회한다.")
    @Test
    void getNoticeDetailById() throws Exception {
        // given
        Long noticeId = 1L;
        String htmlContent = "<div>공지 상세 내용</div>";

        given(noticeService.findByNoticeId(eq(noticeId)))
                .willReturn(new NoticeDetailResponse(noticeId, htmlContent));

        // when & then
        mockMvc.perform(get("/api/v1/notices/{noticeId}", noticeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("공지사항 관련 API")
                                        .description("공지사항 ID로 HTML 상세 정보 조회")
                                        .pathParameters(
                                                parameterWithName("noticeId").description("조회할 공지사항 ID")
                                        )
                                        .build()
                        )
                ));
    }
}
