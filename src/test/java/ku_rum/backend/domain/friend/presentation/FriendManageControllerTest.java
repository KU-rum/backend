package ku_rum.backend.domain.friend.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.friend.application.FriendManageService;
import ku_rum.backend.domain.friend.dto.request.FriendRequest;
import ku_rum.backend.util.RestDocsFieldSnippets;
import ku_rum.backend.util.RestDocsTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FriendManageControllerTest extends RestDocsTestSupport {

    @MockBean
    private FriendManageService friendManageService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @Test
    @DisplayName("친구 요청 API")
    @WithMockUser
    void sendFriendRequest() throws Exception {
        FriendRequest friendSendRequest = new FriendRequest(1L);
        doNothing().when(friendManageService).requestFriend(friendSendRequest);

        mockMvc.perform(post("/api/v1/friends/request")

                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpGJdOigSKjxMIab0cV06xFjSpwrq70")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friendSendRequest)))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("친구 요청")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .requestFields(
                                        fieldWithPath("receiverId").type(JsonFieldType.NUMBER)
                                                .description("친구 요청 대상 유저 ID")
                                )
                                .responseFields(RestDocsFieldSnippets.COMMON_RESPONSE_FIELDS)
                                .build())
                ));
    }

    @Test
    @DisplayName("친구 요청 수락 API")
    @WithMockUser
    void acceptFriendRequest() throws Exception {
        Long requestId = 1L;
        FriendRequest friendRequest = new FriendRequest(1L);
        doNothing().when(friendManageService).respondToFriend(friendRequest, true);

        mockMvc.perform(put("/api/v1/friends/accept")
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpGJdOigSKjxMIab0cV06xFjSpwrq70")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friendRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("친구 요청 수락")
                                .requestFields(
                                        fieldWithPath("receiverId").type(JsonFieldType.NUMBER).description("상대방 유저 ID")
                                )
                                .responseFields(RestDocsFieldSnippets.COMMON_RESPONSE_FIELDS)
                                .build())
                ));
    }

    @Test
    @DisplayName("친구 요청 거절 API")
    @WithMockUser
    void rejectFriendRequest() throws Exception {
        Long requestId = 1L;
        FriendRequest request = new FriendRequest(requestId); // FriendRequest DTO에 맞게 생성자/빌더 필요

        doNothing().when(friendManageService).respondToFriend(request, false);

        mockMvc.perform(put("/api/v1/friends/reject")
                        .header("Authorization", "Bearer your.jwt.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestId\":1}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("친구 요청 거절")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .requestFields(
                                        fieldWithPath("requestId").description("거절할 친구 요청 ID")
                                )
                                .responseFields(RestDocsFieldSnippets.COMMON_RESPONSE_FIELDS)
                                .build())
                ));
    }


    @Test
    @DisplayName("보낸 친구 요청 삭제 API")
    @WithMockUser
    void deleteFriendRequest() throws Exception {
        Long requestId = 1L;
        FriendRequest request = new FriendRequest(requestId); // FriendRequest DTO에 맞게 생성자/빌더 필요

        doNothing().when(friendManageService).deleteSentRequest(request);

        mockMvc.perform(delete("/api/v1/friends/request")
                        .header("Authorization", "Bearer your.jwt.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestId\":1}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("보낸 친구 요청 삭제")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .requestFields(
                                        fieldWithPath("requestId").description("삭제할 친구 요청 ID")
                                )
                                .responseFields(RestDocsFieldSnippets.COMMON_RESPONSE_FIELDS)
                                .build())
                ));
    }

    @Test
    @DisplayName("친구 삭제 API")
    @WithMockUser
    void deleteFriend() throws Exception {
        Long friendId = 1L;

        // friendManageService.deleteFriend(friendId)를 void로 호출하는 경우
        doNothing().when(friendManageService).deleteFriend(friendId);

        mockMvc.perform(delete("/api/v1/friends/{friendId}", friendId)
                        .header("Authorization", "Bearer your.jwt.token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(RestDocsTestUtils.expectCommonSuccess())
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("친구 관련 API")
                                .description("친구 삭제")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰")
                                )
                                .pathParameters(
                                        parameterWithName("friendId").description("삭제할 친구의 사용자 ID")
                                )
                                .responseFields(RestDocsFieldSnippets.COMMON_RESPONSE_FIELDS)
                                .build())
                ));

    }

}