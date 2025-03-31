package ku_rum.backend.domain.notice.domain;

import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.notice.application.ViewCountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class NoticeViewCountControllerTest extends RestDocsTestSupport {

    @Mock
    private ViewCountService viewCountService;

    private MockMvc mockMvc;

    @Test
    void testIncreaseViewCount() throws Exception {
        String url = "test-url";
        String responseMessage = url + "에 대해 조회수가 증가되었습니다.";

        doNothing().when(viewCountService).enqueueLockRequest(url);

        mockMvc.perform(get("/api/v1/notices/url")
                        .param("url", url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(responseMessage));
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
