package ku_rum.backend.domain.rank.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.rank.application.RankService;
import ku_rum.backend.domain.rank.application.response.GetPlaceRankResponse;
import ku_rum.backend.domain.rank.application.response.GetPlaceUserRankResponse;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(RankController.class)
@ActiveProfiles("test")
public class RankControllerTest extends RestDocsTestSupport {

    @MockBean
    RankService rankService;

    @MockBean
    private ApiLogRepository apiLogRepository;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @DisplayName("장소 공유 순위를 확인한다")
    @Test
    void getPlaceUserRank() throws Exception {
        //given
        String placeName = "상허기념도서관";
        int count = 5;
        GetPlaceUserRankResponse getPlaceUserRankResponse = new GetPlaceUserRankResponse(List.of(placeName), count);
        List<GetPlaceUserRankResponse> response = List.of(getPlaceUserRankResponse);

        given(rankService.getPlaceUserRank(any(CustomUserDetails.class)))
                .willReturn(response);

        //when
        mockMvc.perform(get("/api/v1/places/users/ranks")
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70"))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("지도 장소 유저 랭킹 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .build())));

    }

    @DisplayName("친구 장소 공유 순위를 확인한다")
    @Test
    void getPlaceFriendRank() throws Exception {
        //given
        String placeName = "상허기념도서관";
        Long friendId = 2L;
        int count = 5;
        GetPlaceUserRankResponse getPlaceUserRankResponse = new GetPlaceUserRankResponse(List.of(placeName), count);
        List<GetPlaceUserRankResponse> response = List.of(getPlaceUserRankResponse);

        given(rankService.getPlaceFriendRank(any(CustomUserDetails.class), eq(friendId)))
                .willReturn(response);

        //when
        mockMvc.perform(get("/api/v1/places/users/ranks/{friendId}", friendId)
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70"))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("지도 장소 친구 랭킹 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .pathParameters(
                                        RequestDocumentation.parameterWithName("friendId").description("친구id")
                                )
                                .build())));

    }

    @DisplayName("특정 장소의 랭킹을 구간별로 조회한다")
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void getPlaceRank() throws Exception {
        // given
        Long placeId = 75L;
        int startRank = 1;
        int endRank = 10;

        List<GetPlaceRankResponse> response = List.of(
                new GetPlaceRankResponse(2, List.of("테스트8"), 8, false),
                new GetPlaceRankResponse(3, List.of("테스트7"), 7, false),
                new GetPlaceRankResponse(4, List.of("테스트6"), 6, false)
        );

        given(rankService.getPlaceRanks(any(), eq(placeId), eq(startRank), eq(endRank)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/places/ranks/{placeId}", placeId)
                        .param("startRank", String.valueOf(startRank))
                        .param("endRank", String.valueOf(endRank))
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("랭킹 관련 API")
                                .description("특정 장소의 랭킹을 구간별로 조회합니다.")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급받은 엑세스 토큰")
                                )
                                .pathParameters(
                                        RequestDocumentation.parameterWithName("placeId").description("조회할 장소의 ID")
                                )
                                .queryParameters(
                                        RequestDocumentation.parameterWithName("startRank").description("조회 시작 랭크"),
                                        RequestDocumentation.parameterWithName("endRank").description("조회 종료 랭크")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data[].ranking").description("순위"),
                                        fieldWithPath("data[].nickname").description("닉네임 목록"),
                                        fieldWithPath("data[].sharingCount").description("공유 횟수"),
                                        fieldWithPath("data[].isSelf").description("본인 여부")
                                )
                                .build()
                )));
    }
}
