package ku_rum.backend.domain.place.presentation;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.place.application.PlaceHistoryService;
import ku_rum.backend.domain.place.application.PlaceService;
import ku_rum.backend.domain.place.application.PositionService;
import ku_rum.backend.domain.place.application.response.CurrentPositionConfirmResponse;
import ku_rum.backend.domain.place.application.response.CurrentPositionResponse;
import ku_rum.backend.domain.place.application.response.CurrentPositionStatusResponse;
import ku_rum.backend.domain.place.application.response.GetPlaceResponse;
import ku_rum.backend.domain.place.application.response.SearchPlaceHistoryResponse;
import ku_rum.backend.domain.place.application.response.SelectPlaceChipResponse;
import ku_rum.backend.domain.place.domain.CategoryChip;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.PlaceHistory;
import ku_rum.backend.domain.place.domain.PlaceImage;
import ku_rum.backend.domain.place.dto.FriendUserDto;
import ku_rum.backend.domain.place.dto.request.CurrentPositionConfirmRequest;
import ku_rum.backend.domain.place.dto.request.CurrentPositionRequest;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PlaceControllerTest extends RestDocsTestSupport {

    @MockBean
    PositionService positionService;

    @MockBean
    PlaceService placeService;

    @MockBean
    PlaceHistoryService placeHistoryService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .loginId("testuser")
                .email("test@example.com")
                .nickname("테스트용")
                .build();
        CustomUserDetails userDetails = CustomUserDetails.from(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
    }

    @DisplayName("현재 유저의 위치 공유여부를 확인한다.")
    @Test
    void getCurrentPositionStatus() throws Exception {
        //given
        CurrentPositionStatusResponse response = new CurrentPositionStatusResponse(Boolean.FALSE, "공학관");
        given(positionService.getCurrentPositionStatus(any(CustomUserDetails.class)))
                .willReturn(response);

        //when
        mockMvc.perform(get("/api/v1/places/sharing/status")
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70"))

                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.isActive").value(false))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("위치 공유 상태 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .build())));
    }

    @DisplayName("현재 유저의 위치에 해당하는 공유 건물을 조회한다")
    @Test
    void getCurrentPosition() throws Exception {
        //given
        String placeName = "상허기념도서관";
        CurrentPositionRequest request = new CurrentPositionRequest(BigDecimal.valueOf(37.53712),
                BigDecimal.valueOf(127.085795));
        CurrentPositionResponse response = new CurrentPositionResponse(placeName);

        given(positionService.getCurrentPosition(any(CustomUserDetails.class), eq(request)))
                .willReturn(response);

        //when
        mockMvc.perform(post("/api/v1/places/sharing")
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.placeName").value(placeName))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("위치 공유값 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .build())));
    }

    @DisplayName("현재 유저의 위치를 공유한다")
    @Test
    void sharingPosition() throws Exception {
        //given
        String placeName = "상허기념도서관";
        CurrentPositionConfirmRequest request = new CurrentPositionConfirmRequest(placeName);
        CurrentPositionConfirmResponse response = new CurrentPositionConfirmResponse(placeName);

        given(positionService.confirmCurrentPosition(any(CustomUserDetails.class), eq(request)))
                .willReturn(response);

        //when
        mockMvc.perform(post("/api/v1/places/sharing/confirm")
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.placeName").value(placeName))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("위치 공유")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .build())));
    }

    @DisplayName("현재 유저의 위치를 공유한다")
    @Test
    void disableSharingPosition() throws Exception {
        //given
        doNothing().when(positionService).disableSharingPosition(any(CustomUserDetails.class));

        //when
        mockMvc.perform(delete("/api/v1/places/sharing/confirm")
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70"))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("위치 공유 중지")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .build())));
    }

    @DisplayName("칩에 해당 하는 정보를 조회한다")
    @Test
    void selectChip() throws Exception {
        //given
        String name = "상허기념도서관";
        Place place = Place.builder()
                .placeId(1L)
                .categoryChip(CategoryChip.K_CUBE)
                .name(name)
                .subName("상허기념도서관 K-CUBE")
                .content("상허기념도서관 K-CUBE입니다")
                .latitude(BigDecimal.valueOf(37.541941000))
                .longitude(BigDecimal.valueOf(127.073784000))
                .build();

        List<SelectPlaceChipResponse> response = List.of(SelectPlaceChipResponse.from(place));

        given(placeService.selectChipWithUser(any(CustomUserDetails.class), eq(CategoryChip.K_CUBE)))
                .willReturn(response);

        //when
        mockMvc.perform(get("/api/v1/places")
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70")
                        .param("chip", "K_CUBE"))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value(name))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("지도 칩 정보 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .queryParameters(parameterWithName("chip").description("칩 이름"))
                                .build())));
    }

    @DisplayName("장소를 조회한다")
    @Test
    void getPlace() throws Exception {
        //given
        String name = "상허기념도서관";
        Long placeId = 1L;
        Place place = Place.builder()
                .placeId(placeId)
                .categoryChip(CategoryChip.K_CUBE)
                .name(name)
                .subName("상허기념도서관 K-CUBE")
                .content("상허기념도서관 K-CUBE입니다")
                .latitude(BigDecimal.valueOf(37.541941000))
                .longitude(BigDecimal.valueOf(127.073784000))
                .build();

        List<FriendUserDto> friendUserDtos = List.of(new FriendUserDto(1L, "닉네임", "url", place));
        PlaceImage placeImage = PlaceImage.builder()
                .placeImageId(1L)
                .place(place)
                .imageUrl("URL")
                .build();

        List<PlaceImage> placeImages = List.of(placeImage);
        GetPlaceResponse response = GetPlaceResponse.of(place, friendUserDtos, placeImages, null);

        given(placeService.getPlaceWithUser(any(CustomUserDetails.class), eq(placeId)))
                .willReturn(response);

        //when
        mockMvc.perform(get("/api/v1/places/{placeId}", placeId)
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70"))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(name))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("정보 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .pathParameters(
                                        RequestDocumentation.parameterWithName("placeId").description("건물id")
                                )
                                .build())));

    }

    @DisplayName("장소 검색 기록를 확인한다")
    @Test
    void searchPlaceHistory() throws Exception {
        //given
        Long placeId = 1L;
        String name = "상허기념도서관";
        Place place = Place.builder()
                .placeId(placeId)
                .categoryChip(CategoryChip.K_CUBE)
                .name(name)
                .subName("상허기념도서관 K-CUBE")
                .content("상허기념도서관 K-CUBE입니다")
                .latitude(BigDecimal.valueOf(37.541941000))
                .longitude(BigDecimal.valueOf(127.073784000))
                .build();
        PlaceHistory placeHistory = PlaceHistory.builder()
                .placeHistoryId(1L)
                .name(name)
                .build();
        SearchPlaceHistoryResponse response = SearchPlaceHistoryResponse.from(placeHistory);

        given(placeHistoryService.searchPlaceHistory(any(CustomUserDetails.class)))
                .willReturn(List.of(response));

        //when
        mockMvc.perform(get("/api/v1/places/search/history")
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70"))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].placeHistoryId").value(placeId))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("지도 검색 히스토리 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .build())));
    }

    @DisplayName("장소 검색 기록를 삭제한다")
    @Test
    void deleteSearchHistory() throws Exception {
        //given
        Long placeHistoryId = 1L;
        doNothing().when(placeHistoryService).deletePlaceHistory(eq(placeHistoryId), any(CustomUserDetails.class));
        //then
        mockMvc.perform(delete("/api/v1/places/search/history/{placeHistoryId}", placeHistoryId)
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70"))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("지도 검색 히스토리 삭제")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .pathParameters(
                                        RequestDocumentation.parameterWithName("placeHistoryId")
                                                .description("검색 히스토리id")
                                )
                                .build())));
    }

    @DisplayName("장소 검색 기록를 전부 삭제한다")
    @Test
    void deleteAllSearchHistory() throws Exception {
        //given
        Long placeHistoryId = 1L;
        doNothing().when(placeHistoryService).deletePlaceHistory(any(CustomUserDetails.class));
        //then
        mockMvc.perform(delete("/api/v1/places/search/history")
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpㄴGJdOigSKjxMIab0cV06xFjSpwrq70"))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("지도 관련 API")
                                .description("지도 검색 히스토리 전부 삭제")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .build())));
    }
}