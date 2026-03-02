package ku_rum.backend.domain.friend.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsUnitTestSupport;
import ku_rum.backend.domain.friend.application.FriendReportService;
import ku_rum.backend.domain.friend.dto.request.FriendBlockRequest;
import ku_rum.backend.domain.friend.dto.request.FriendReportRequest;
import ku_rum.backend.util.RestDocsFieldSnippets;
import ku_rum.backend.util.RestDocsTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(FriendReportController.class)
@ActiveProfiles("test")
class FriendReportControllerTest extends RestDocsUnitTestSupport {

    @MockBean
    private FriendReportService friendReportService;

    @DisplayName("친구 차단 API")
    @Test
    @WithMockUser
    void blockFriend() throws Exception {
        // given
        FriendBlockRequest request = new FriendBlockRequest(1L);

        doNothing().when(friendReportService).blockFriend(any(FriendBlockRequest.class));

        // when & then
        mockMvc.perform(patch("/api/v1/friends/block")
                        .header("Authorization",
                                "Bearer access_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andExpect(jsonPath("$.data").value("차단이 완료되었습니다."))
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("친구 차단")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .requestFields(
                                        fieldWithPath("reportId").type(JsonFieldType.NUMBER).description("차단 대상 유저 ID")
                                )
                                .responseFields(RestDocsFieldSnippets.COMMON_RESPONSE_FIELDS_WITH_DATA)
                                .build())
                ));
    }

    @DisplayName("친구 신고 API")
    @Test
    @WithMockUser
    void reportFriend() throws Exception {
        // given
        FriendReportRequest request = new FriendReportRequest(1L, "욕설을 사용했습니다.");

        doNothing().when(friendReportService).reportFriend(any(FriendReportRequest.class));

        // when & then
        mockMvc.perform(patch("/api/v1/friends/report")
                        .header("Authorization",
                                "Bearer access_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andExpect(jsonPath("$.data").value("신고가 완료되었습니다."))
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("친구 신고")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .requestFields(
                                        fieldWithPath("reportId").type(JsonFieldType.NUMBER).description("신고 대상 유저 ID"),
                                        fieldWithPath("reason").type(JsonFieldType.STRING).description("신고 사유")
                                )
                                .responseFields(RestDocsFieldSnippets.COMMON_RESPONSE_FIELDS_WITH_DATA)
                                .build())
                ));
    }
}