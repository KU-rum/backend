package ku_rum.backend.domain.rank.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.rank.application.RankService;
import ku_rum.backend.domain.rank.application.response.GetPlaceUserRankResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class RankControllerTest extends RestDocsTestSupport {

    @MockBean
    RankService rankService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

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
        int count = 5;
        GetPlaceUserRankResponse getPlaceUserRankResponse = new GetPlaceUserRankResponse(List.of(placeName), count);
        List<GetPlaceUserRankResponse> response = List.of(getPlaceUserRankResponse);

        given(rankService.getPlaceUserRank(any(CustomUserDetails.class)))
                .willReturn(response);

        //when
        mockMvc.perform(get("/api/v1/places/users/ranks/2")
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
                                .build())));

    }
}
