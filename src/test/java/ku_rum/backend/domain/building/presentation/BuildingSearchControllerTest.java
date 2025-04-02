package ku_rum.backend.domain.building.presentation;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.building.application.BuildingSearchService;
import ku_rum.backend.domain.building.dto.response.BuildingResponse;
import ku_rum.backend.domain.category.domain.Category;
import ku_rum.backend.domain.category.dto.response.CategoryDetailResponse;
import ku_rum.backend.domain.menu.dto.response.MenuSimpleResponse;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.application.UserValidator;
import ku_rum.backend.global.batch.BatchScheduler;
import ku_rum.backend.global.utill.RedisUtil;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.json.JsonType;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(BuildingSearchController.class)
@ActiveProfiles("test")
class BuildingSearchControllerTest extends RestDocsTestSupport {

    @MockBean
    private BuildingSearchService buildingSearchService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private ApiLogRepository apiLogRepository;

    @MockBean
    private UserValidator userValidator;

    @MockBean
    private JobLauncher jobLauncher;

    @MockBean
    private Job job; // 실제 배치 Job

    @MockBean
    private JobRepository jobRepository; // JobRepository Mock

    @MockBean
    private JobExplorer jobExplorer; // JobExplorer Mock

    @MockBean
    private JobOperator jobOperator; // JobExplorer Mock

    @DisplayName("학교의 모든 건물정보를 출력한다.")
    @Test
    @WithMockUser
    void viewAll() throws Exception {
        // given (Mock 데이터 설정)
        List<BuildingResponse> mockBuildings = List.of(
                new BuildingResponse(16L, "공학관", 21L, "공",
                        BigDecimal.valueOf(37.5418220000000), BigDecimal.valueOf(127.0788450000000)),
                new BuildingResponse(1L, "경영관", 2L, "경영",
                        BigDecimal.valueOf(37.5444190000000), BigDecimal.valueOf(127.0763700000000))
        );
        given(buildingSearchService.findAllBuildings()).willReturn(mockBuildings);

        //when then
        mockMvc.perform(get("/api/v1/building/search")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data[0].buildingId").value(16))
                .andExpect(jsonPath("$.data[0].buildingName").value("공학관"))
                .andExpect(jsonPath("$.data[0].buildingNumber").value(21))
                .andExpect(jsonPath("$.data[0].buildingAbbreviation").value("공"))
                .andExpect(jsonPath("$.data[0].latitude").value(37.541822))
                .andExpect(jsonPath("$.data[0].longitude").value(127.078845))
                .andExpect(jsonPath("$.data[1].buildingId").value(1))
                .andExpect(jsonPath("$.data[1].buildingName").value("경영관"))
                .andExpect(jsonPath("$.data[1].buildingNumber").value(2))
                .andExpect(jsonPath("$.data[1].buildingAbbreviation").value("경영"))
                .andExpect(jsonPath("$.data[1].latitude").value(37.544419))
                .andExpect(jsonPath("$.data[1].longitude").value(127.076370))

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("모든 건물 정보 출력")
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonType.NUMBER)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 메시지 (OK)"),
                                                fieldWithPath("data[].buildingId")
                                                        .type(JsonType.NUMBER).description("빌딩 ID"),
                                                fieldWithPath("data[].buildingName")
                                                        .type(JsonType.STRING)
                                                        .description("빌딩 이름"),
                                                fieldWithPath("data[].buildingNumber")
                                                        .type(JsonType.NUMBER)
                                                        .description("빌딩 번호"),
                                                fieldWithPath("data[].buildingAbbreviation")
                                                        .type(JsonType.STRING)
                                                        .description("빌딩 약어"),
                                                fieldWithPath("data[].latitude")
                                                        .type(JsonType.NUMBER)
                                                        .description("위도"),
                                                fieldWithPath("data[].longitude")
                                                        .type(JsonType.NUMBER)
                                                        .description("경도")
                                        ).build())));

        verify(buildingSearchService).findAllBuildings();
        verify(userValidator).validateUserDetails(any());

    }


    @DisplayName("특정 건물번호로 해당 건물정보를 출력한다.")
    @Test
    @WithMockUser
    void viewBuildingByNumber() throws Exception {
        // given (Mock 데이터 설정)
        List<BuildingResponse> mockBuildings = List.of(
                new BuildingResponse(16L, "공학관", 21L, "공",
                        BigDecimal.valueOf(37.5418220000000), BigDecimal.valueOf(127.0788450000000)),
                new BuildingResponse(1L, "경영관", 2L, "경영",
                        BigDecimal.valueOf(37.5444190000000), BigDecimal.valueOf(127.0763700000000))
        );
        given(buildingSearchService.viewBuildingByNumber(21L)).willReturn(mockBuildings.get(0));

        //when then
        mockMvc.perform(get("/api/v1/building/search/number?number=21")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data.buildingId").value(16))
                .andExpect(jsonPath("$.data.buildingName").value("공학관"))
                .andExpect(jsonPath("$.data.buildingNumber").value(21))
                .andExpect(jsonPath("$.data.buildingAbbreviation").value("공"))
                .andExpect(jsonPath("$.data.latitude").value(37.541822))
                .andExpect(jsonPath("$.data.longitude").value(127.078845))

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("특정 건물번호로 건물 정보 출력")
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonType.NUMBER)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 메시지 (OK)"),
                                                fieldWithPath("data.buildingId")
                                                        .type(JsonType.NUMBER).description("빌딩 ID"),
                                                fieldWithPath("data.buildingName")
                                                        .type(JsonType.STRING)
                                                        .description("빌딩 이름"),
                                                fieldWithPath("data.buildingNumber")
                                                        .type(JsonType.NUMBER)
                                                        .description("빌딩 번호"),
                                                fieldWithPath("data.buildingAbbreviation")
                                                        .type(JsonType.STRING)
                                                        .description("빌딩 약어"),
                                                fieldWithPath("data.latitude")
                                                        .type(JsonType.NUMBER)
                                                        .description("위도"),
                                                fieldWithPath("data.longitude")
                                                        .type(JsonType.NUMBER)
                                                        .description("경도")
                                        ).build())));


    }


    @DisplayName("특정 건물이름/줄임말로 해당 건물정보를 출력한다.")
    @Test
    @WithMockUser
    void viewBuildingByName() throws Exception {
        // given (Mock 데이터 설정)
        List<BuildingResponse> mockBuildings = List.of(
                new BuildingResponse(16L, "공학관", 21L, "공",
                        BigDecimal.valueOf(37.5418220000000), BigDecimal.valueOf(127.0788450000000)),
                new BuildingResponse(1L, "경영관", 2L, "경영",
                        BigDecimal.valueOf(37.5444190000000), BigDecimal.valueOf(127.0763700000000))
        );
        given(buildingSearchService.viewBuildingByName("공")).willReturn(mockBuildings.get(0));

        //when then
        mockMvc.perform(get("/api/v1/building/search/name?name=공")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data.buildingId").value(16))
                .andExpect(jsonPath("$.data.buildingName").value("공학관"))
                .andExpect(jsonPath("$.data.buildingNumber").value(21))
                .andExpect(jsonPath("$.data.buildingAbbreviation").value("공"))
                .andExpect(jsonPath("$.data.latitude").value(37.541822))
                .andExpect(jsonPath("$.data.longitude").value(127.078845))

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("건물이름/줄임말로 건물정보 검색")
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonType.NUMBER)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 메시지 (OK)"),
                                                fieldWithPath("data.buildingId")
                                                        .type(JsonType.NUMBER).description("빌딩 ID"),
                                                fieldWithPath("data.buildingName")
                                                        .type(JsonType.STRING)
                                                        .description("빌딩 이름"),
                                                fieldWithPath("data.buildingNumber")
                                                        .type(JsonType.NUMBER)
                                                        .description("빌딩 번호"),
                                                fieldWithPath("data.buildingAbbreviation")
                                                        .type(JsonType.STRING)
                                                        .description("빌딩 약어"),
                                                fieldWithPath("data.latitude")
                                                        .type(JsonType.NUMBER)
                                                        .description("위도"),
                                                fieldWithPath("data.longitude")
                                                        .type(JsonType.NUMBER)
                                                        .description("경도")
                                        ).build())));


    }


    @DisplayName("특정 카테고리명으로 해당되는 건물정보들을 출력한다.")
    @Test
    @WithMockUser
    void viewBuildingByCategory() throws Exception {
        // given (Mock 데이터 설정)
        List<BuildingResponse> mockBuildings = List.of(
                new BuildingResponse(16L, "레스티오_공대점", 21L, "레스티오_공",
                        BigDecimal.valueOf(37.5418220000000), BigDecimal.valueOf(127.0788450000000)),
                new BuildingResponse(1L, "레스티오_동생대점", 2L, "레스티오_동",
                        BigDecimal.valueOf(37.5444190000000), BigDecimal.valueOf(127.0763700000000))
        );
        given(buildingSearchService.viewBuildingByCategory("레스티오")).willReturn(mockBuildings);

        //when then
        mockMvc.perform(get("/api/v1/building/search/category?category=레스티오")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data[0].buildingId").value(16))
                .andExpect(jsonPath("$.data[0].buildingName").value("레스티오_공대점"))
                .andExpect(jsonPath("$.data[0].buildingNumber").value(21))
                .andExpect(jsonPath("$.data[0].buildingAbbreviation").value("레스티오_공"))
                .andExpect(jsonPath("$.data[0].latitude").value(37.541822))
                .andExpect(jsonPath("$.data[0].longitude").value(127.078845))
                .andExpect(jsonPath("$.data[1].buildingId").value(1))
                .andExpect(jsonPath("$.data[1].buildingName").value("레스티오_동생대점"))
                .andExpect(jsonPath("$.data[1].buildingNumber").value(2))
                .andExpect(jsonPath("$.data[1].buildingAbbreviation").value("레스티오_동"))
                .andExpect(jsonPath("$.data[1].latitude").value(37.544419))
                .andExpect(jsonPath("$.data[1].longitude").value(127.076370))

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("특정 카테고리명으로 건물정보 검색")
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonType.NUMBER)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 메시지 (OK)"),
                                                fieldWithPath("data[].buildingId")
                                                        .type(JsonType.NUMBER).description("빌딩 ID"),
                                                fieldWithPath("data[].buildingName")
                                                        .type(JsonType.STRING)
                                                        .description("빌딩 이름"),
                                                fieldWithPath("data[].buildingNumber")
                                                        .type(JsonType.NUMBER)
                                                        .description("빌딩 번호"),
                                                fieldWithPath("data[].buildingAbbreviation")
                                                        .type(JsonType.STRING)
                                                        .description("빌딩 약어"),
                                                fieldWithPath("data[].latitude")
                                                        .type(JsonType.NUMBER)
                                                        .description("위도"),
                                                fieldWithPath("data[].longitude")
                                                        .type(JsonType.NUMBER)
                                                        .description("경도")
                                        ).build())));


    }

    @DisplayName("fulltext search로 건물명, 카테고리명으로 빌딩 정보를 출력한다.")
    @Test
    @WithMockUser
    void viewAvailableTextNameList() throws Exception {
        // given (Mock 데이터 설정)
        List<BuildingResponse> mockBuildings = List.of(
                new BuildingResponse(16L, "레스티오_공대점", 21L, "레스티오_공",
                        BigDecimal.valueOf(37.5418220000000), BigDecimal.valueOf(127.0788450000000)),
                new BuildingResponse(1L, "레스티오_동생대점", 2L, "레스티오_동",
                        BigDecimal.valueOf(37.5444190000000), BigDecimal.valueOf(127.0763700000000))
        );
        given(buildingSearchService.searchAvailableText("레스")).willReturn(mockBuildings);

        //when then
        mockMvc.perform(get("/api/v1/building/search/text?text=레스")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data[0].buildingId").value(16))
                .andExpect(jsonPath("$.data[0].buildingName").value("레스티오_공대점"))
                .andExpect(jsonPath("$.data[0].buildingNumber").value(21))
                .andExpect(jsonPath("$.data[0].buildingAbbreviation").value("레스티오_공"))
                .andExpect(jsonPath("$.data[0].latitude").value(37.541822))
                .andExpect(jsonPath("$.data[0].longitude").value(127.078845))
                .andExpect(jsonPath("$.data[1].buildingId").value(1))
                .andExpect(jsonPath("$.data[1].buildingName").value("레스티오_동생대점"))
                .andExpect(jsonPath("$.data[1].buildingNumber").value(2))
                .andExpect(jsonPath("$.data[1].buildingAbbreviation").value("레스티오_동"))
                .andExpect(jsonPath("$.data[1].latitude").value(37.544419))
                .andExpect(jsonPath("$.data[1].longitude").value(127.076370))

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("특정 카테고리명으로 건물정보 검색")
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonType.NUMBER)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 메시지 (OK)"),
                                                fieldWithPath("data[].buildingId")
                                                        .type(JsonType.NUMBER).description("빌딩 ID"),
                                                fieldWithPath("data[].buildingName")
                                                        .type(JsonType.STRING)
                                                        .description("빌딩 이름"),
                                                fieldWithPath("data[].buildingNumber")
                                                        .type(JsonType.NUMBER)
                                                        .description("빌딩 번호"),
                                                fieldWithPath("data[].buildingAbbreviation")
                                                        .type(JsonType.STRING)
                                                        .description("빌딩 약어"),
                                                fieldWithPath("data[].latitude")
                                                        .type(JsonType.NUMBER)
                                                        .description("위도"),
                                                fieldWithPath("data[].longitude")
                                                        .type(JsonType.NUMBER)
                                                        .description("경도")
                                        ).build())));

    }

    @DisplayName("특정 카테고리명과 건물번호로 해당 카테고리의 디테일 정보를 출력한다.")
    @Test
    @WithMockUser
    void viewBuildingByCategoryInBuilding() throws Exception {
        // given (Mock 데이터 설정)
        Category cafeteria = Category.of("학생 식당");
        List<MenuSimpleResponse> menuList_info = List.of(
                MenuSimpleResponse.builder().name("마라탕").price(4000L).imageUrl("NONE").build(),
                MenuSimpleResponse.builder().name("쌀국수").price(4500L).imageUrl("NONE").build(),
                MenuSimpleResponse.builder().name("순대").price(5000L).imageUrl("NONE").build()
        );

        CategoryDetailResponse<MenuSimpleResponse> categoryDetailResponse = CategoryDetailResponse.<MenuSimpleResponse>builder()
                .category(cafeteria.getName())
                .floor(null)
                .detailList(menuList_info)
                .build();
        given(buildingSearchService.viewBuildingDetailByCategory("학생 식당", 36L))
                .willReturn(categoryDetailResponse);

        String requestJson = """
                    {
                        "category": "학생 식당",
                        "buildingId": 36
                    }
                """;

        //when then
        mockMvc.perform(post("/api/v1/building/search/detail")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data.category").value("학생 식당"))
                .andExpect(jsonPath("$.data.floor").isEmpty())

                .andExpect(jsonPath("$.data.detailList[0].name").value("마라탕"))
                .andExpect(jsonPath("$.data.detailList[0].price").value(4000L))
                .andExpect(jsonPath("$.data.detailList[1].name").value("쌀국수"))
                .andExpect(jsonPath("$.data.detailList[1].price").value(4500L))
                .andExpect(jsonPath("$.data.detailList[2].name").value("순대"))
                .andExpect(jsonPath("$.data.detailList[2].price").value(5000L))

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("특정 카테고리명으로 디테일 정보 보기")
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonType.NUMBER)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonType.STRING)
                                                        .description("올바른 인증코드 시 메시지 (OK)"),
                                                fieldWithPath("data.category")
                                                        .type(JsonType.STRING)
                                                        .description("카테고리명"),
                                                fieldWithPath("data.floor")
                                                        .type(JsonType.NUMBER)
                                                        .optional()
                                                        .description("층 정보 (없을 수도 있음)"),
                                                fieldWithPath("data.detailList[].name")
                                                        .type(JsonType.STRING)
                                                        .description("메뉴 이름"),
                                                fieldWithPath("data.detailList[].price")
                                                        .type(JsonType.NUMBER)
                                                        .description("메뉴 가격"),
                                                fieldWithPath("data.detailList[].imageUrl")
                                                        .type(JsonType.STRING)
                                                        .description("메뉴 이미지 URL")
                                        ).build())));


    }


}