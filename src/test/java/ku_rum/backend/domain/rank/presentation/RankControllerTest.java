package ku_rum.backend.domain.rank.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
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
import ku_rum.backend.domain.rank.application.response.GetPlaceRankPaginationResponse;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @WithMockUser(username = "testUser", roles = {"USER"})
    void getPlaceFriendRank() throws Exception {
        // given
        Long friendId = 2L;
        String placeName = "상허기념도서관";
        int count = 5;

        CustomUserDetails userDetails = CustomUserDetails.of(
                1L,
                "testUser",
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                "password",
                false
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        ));
        SecurityContextHolder.setContext(context);
        GetPlaceUserRankResponse friendRankResponse = new GetPlaceUserRankResponse(List.of(placeName), count);
        List<GetPlaceUserRankResponse> response = List.of(friendRankResponse);

        given(rankService.getPlaceFriendRank(any(CustomUserDetails.class), eq(friendId)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/places/users/{friendId}/ranks", friendId)
                        .header("Authorization", "Bearer test-access-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("지도 장소 친구 랭킹 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰")
                                )
                                .pathParameters(
                                        RequestDocumentation.parameterWithName("friendId").description("조회할 친구 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data[].name").description("친구가 공유한 장소 목록"),
                                        fieldWithPath("data[].sharingCount").description("친구의 공유 횟수")
                                )
                                .build()
                )));
    }

    @DisplayName("특정 장소의 랭킹을 구간별로 조회한다")
    @Test
    void getPlaceRank() throws Exception {
        // given
        Long placeId = 75L;

        List<GetPlaceRankResponse> getPlaceRankResponses = List.of(
                new GetPlaceRankResponse(2, "테스트8", 8),
                new GetPlaceRankResponse(3, "테스트7", 7),
                new GetPlaceRankResponse(4, "테스트6", 6)
        );
        GetPlaceRankPaginationResponse response = GetPlaceRankPaginationResponse.of(getPlaceRankResponses, false,
                "4_2");
        given(rankService.getPlaceRanks(eq(placeId), any()))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/places/{placeId}/ranks", placeId)
                        .param("lastKnown", "")
                        .param("limit", "3"))

                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("랭킹 관련 API")
                                .description("특정 장소의 랭킹을 구간별로 조회합니다.")
                                .pathParameters(
                                        RequestDocumentation.parameterWithName("placeId").description("조회할 장소의 ID")
                                )
                                .queryParameters(
                                        RequestDocumentation.parameterWithName("lastKnown").description("가장 최근 커서"),
                                        RequestDocumentation.parameterWithName("limit").description("갯수")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data.ranks[].ranking").description("순위"),
                                        fieldWithPath("data.ranks[].nickname").description("닉네임 목록"),
                                        fieldWithPath("data.ranks[].sharingCount").description("공유 횟수"),
                                        fieldWithPath("data.hasNext").description("마지막 여부"),
                                        fieldWithPath("data.nextCursor").description("nextCursor")
                                )
                                .build()
                )));
    }

    @DisplayName("사용자의 특정 장소 내랭킹을 조회한다")
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void getPlaceMyRank() throws Exception {
        // given
        Long placeId = 75L;

        CustomUserDetails userDetails = CustomUserDetails.of(
                1L,
                "testUser",
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                "password",
                false
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        ));
        SecurityContextHolder.setContext(context);

        GetPlaceRankResponse myRankResponse = new GetPlaceRankResponse(5, "testUser", 10);
        given(rankService.getUserPlaceRank(eq(userDetails.getUserId()), eq(placeId)))
                .willReturn(myRankResponse);

        // when & then
        mockMvc.perform(get("/api/v1/places/{placeId}/ranks/me", placeId)
                        .header("Authorization", "Bearer test-access-token")
                        .principal(new UsernamePasswordAuthenticationToken(
                                userDetails,
                                userDetails.getPassword(),
                                userDetails.getAuthorities()
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("랭킹 관련 API")
                                .description("사용자의 특정 장소 랭킹을 조회합니다.")
                                .pathParameters(
                                        parameterWithName("placeId").description("조회할 장소의 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data.ranking").description("순위"),
                                        fieldWithPath("data.nickname").description("닉네임"),
                                        fieldWithPath("data.sharingCount").description("공유 횟수")
                                )
                                .build()
                )));
    }

    @DisplayName("특정 장소의 상위 랭킹을 조회한다")
    @Test
    void getPlaceTopRank() throws Exception {
        // given
        Long placeId = 75L;

        List<GetPlaceRankResponse> topRanks = List.of(
                new GetPlaceRankResponse(1, "UserA", 15),
                new GetPlaceRankResponse(2, "UserB", 12),
                new GetPlaceRankResponse(3, "UserC", 10)
        );

        given(rankService.getPlaceTopRank(eq(placeId))).willReturn(topRanks);

        // when & then
        mockMvc.perform(get("/api/v1/places/{placeId}/top", placeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("랭킹 관련 API")
                                .description("특정 장소의 상위 랭킹을 조회합니다.")
                                .pathParameters(
                                        parameterWithName("placeId").description("조회할 장소의 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data[].ranking").description("순위"),
                                        fieldWithPath("data[].nickname").description("닉네임"),
                                        fieldWithPath("data[].sharingCount").description("공유 횟수")
                                )
                                .build()
                )));
    }


}
