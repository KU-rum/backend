package ku_rum.backend.domain.notice.domain;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.notice.application.ViewCountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class NoticeViewCountControllerTest extends RestDocsTestSupport {

    @MockBean
    private ViewCountService viewCountService;

    @DisplayName("공지사항 조회수 증가 테스트")
    @Test
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
    void testMostViewedNotices() throws Exception {
        when(viewCountService.mostViewedNotices()).thenReturn(List.of("Notice 1", "Notice 2", "Notice 3"));

        mockMvc.perform(get("/api/v1/notices/popular/url")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("Notice 1"))
                .andExpect(jsonPath("$.data[1]").value("Notice 2"))
                .andExpect(jsonPath("$.data[2]").value("Notice 3"));
    }
}
