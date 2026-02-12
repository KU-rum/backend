package ku_rum.backend.domain.friend.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.friend.application.FriendQueryService;
import ku_rum.backend.domain.friend.dto.response.FriendListResponse;
import ku_rum.backend.domain.friend.dto.response.FriendSearchResponse;
import ku_rum.backend.domain.friend.dto.response.ReceivedFriendResponse;
import ku_rum.backend.domain.friend.dto.response.SentFriendResponse;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import ku_rum.backend.util.RestDocsFieldSnippets;
import ku_rum.backend.util.RestDocsTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

//@SpringBootTest
@WebMvcTest(FriendQueryController.class)
@ActiveProfiles("test")
class FriendQueryControllerTest extends RestDocsTestSupport {


    @MockBean
    private FriendQueryService friendQueryService;

    @MockBean
    private ApiLogRepository apiLogRepository;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @Test
    @DisplayName("친구 목록 조회 API")
    @WithMockUser
    void getFriendList() throws Exception {
        // given
        List<FriendListResponse> mockList = List.of(
                new FriendListResponse(1L, "친구1", "image.com"),
                new FriendListResponse(2L, "친구2", "image.com")
        );
        when(friendQueryService.getFriendList()).thenReturn(mockList);

        // when & then
        mockMvc.perform(get("/api/v1/friends/list")
                        .header("Authorization",
                                "Bearer access_token")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].nickname").value("친구1"))
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("친구 목록 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .responseFields(RestDocsFieldSnippets.withDataFields(List.of(
                                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("친구 ID"),
                                        fieldWithPath("data[].nickname").type(JsonFieldType.STRING)
                                                .description("친구 닉네임"),
                                        fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
                                                .description("프로필 이미지")
                                )))
                                .build())
                ));
    }

    @Test
    @DisplayName("받은 친구 요청 목록 조회 API")
    @WithMockUser
    void getReceivedFriendRequests() throws Exception {
        // given
        List<ReceivedFriendResponse> mockList = List.of(
                new ReceivedFriendResponse(10L, 3L, "요청자A", "image.com"),
                new ReceivedFriendResponse(11L, 4L, "요청자B", "image.com")
        );
        when(friendQueryService.getReceivedPendingRequests()).thenReturn(mockList);

        // when & then
        mockMvc.perform(get("/api/v1/friends/requests/received")
                        .header("Authorization",
                                "Bearer access_token")

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andExpect(jsonPath("$.data[0].requestId").value(10))
                .andExpect(jsonPath("$.data[0].fromUserId").value(3))
                .andExpect(jsonPath("$.data[0].fromUserNickname").value("요청자A"))
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("받은 친구 요청 목록 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .responseFields(RestDocsFieldSnippets.withDataFields(List.of(
                                        fieldWithPath("data[].requestId").type(JsonFieldType.NUMBER)
                                                .description("요청 ID"),
                                        fieldWithPath("data[].fromUserId").type(JsonFieldType.NUMBER)
                                                .description("보낸 사람 ID"),
                                        fieldWithPath("data[].fromUserNickname").type(JsonFieldType.STRING)
                                                .description("보낸 사람 닉네임"),
                                        fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
                                                .description("프로필 이미지")
                                )))
                                .build())
                ));
    }

    @DisplayName("보낸 친구 요청 목록 조회 API")
    @Test
    @WithMockUser
    void getSentRequests() throws Exception {
        // given
        List<SentFriendResponse> mockResponse = List.of(
                new SentFriendResponse(1L, 2L, "receiver1", "image"),
                new SentFriendResponse(2L, 3L, "receiver2", "image2")
        );

        given(friendQueryService.getSentPendingRequests()).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/v1/friends/requests/sent")
                        .header("Authorization", "Bearer your.jwt.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("보낸 친구 요청 목록 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .responseFields(
                                        fieldWithPath("data[].requestId").description("친구 요청 ID"),
                                        fieldWithPath("data[].fromUserId").description("수신자 ID"),
                                        fieldWithPath("data[].fromUserNickname").description("수신자 닉네임"),
                                        fieldWithPath("data[].imageUrl").description("프로필 이미지"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("code").description("응답 코드")
                                )
                                .build())
                ));
    }

    @Test
    @DisplayName("닉네임으로 친구 검색 API")
    @WithMockUser
    void searchFriendByNickname() throws Exception {
        String nickname = "minu";

        // Mock 응답 데이터
        List<FriendSearchResponse> mockResponse = List.of(
                new FriendSearchResponse(1L, "minu1", "https://img1.com", true, true, false),
                new FriendSearchResponse(2L, "minu2", "https://img2.com", false, false, false)
        );
        when(friendQueryService.searchByNickname(nickname)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/friends/search")
                        .header("Authorization", "Bearer your.jwt.token")
                        .param("nickname", nickname))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("닉네임으로 친구 검색")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰")
                                )
                                .queryParameters(
                                        parameterWithName("nickname").description("검색할 닉네임 (부분일치)")
                                )
                                .responseFields(
                                        RestDocsFieldSnippets.COMMON_RESPONSE_FIELDS
                                )
                                .responseFields(RestDocsFieldSnippets.withDataFields(List.of(
                                        fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("유저 ID"),
                                        fieldWithPath("data[].nickname").type(JsonFieldType.STRING)
                                                .description("유저 닉네임"),
                                        fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
                                                .description("프로필 이미지 URL"),
                                        fieldWithPath("data[].requestSent").type(JsonFieldType.BOOLEAN)
                                                .description("친구 요청 보냈는지 여부"),
                                        fieldWithPath("data[].requestReceived").type(JsonFieldType.BOOLEAN)
                                                .description("친구 요청 받았는지 여부"),
                                        fieldWithPath("data[].isFriend").type(JsonFieldType.BOOLEAN)
                                                .description("이미 친구인지 여부")
                                )))
                                .build())
                ));
    }


}