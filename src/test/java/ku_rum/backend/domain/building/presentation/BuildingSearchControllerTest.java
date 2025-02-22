package ku_rum.backend.domain.building.presentation;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.building.application.BuildingSearchService;
import ku_rum.backend.domain.building.dto.response.BuildingResponse;
import ku_rum.backend.domain.category.dto.request.BuildindgCategoryRequest;
import ku_rum.backend.domain.category.dto.response.CategoryDetailResponse;
import ku_rum.backend.domain.menu.response.MenuSimpleResponse;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.global.config.redis.RedisUtil;
import ku_rum.backend.global.log.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.json.JsonType;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(BuildingSearchController.class)
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
    mockMvc.perform(get("/api/v1/buildings/view")
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
    mockMvc.perform(get("/api/v1/buildings/view/searchNumber?number=21")
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
    given(buildingSearchService.viewBuildingByName("공")).willReturn(Optional.of(mockBuildings.get(0)));

    //when then
    mockMvc.perform(get("/api/v1/buildings/view/searchName?name=공")
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


//  @DisplayName("특정 카테고리명으로 해당되는 건물정보들을 출력한다.")
//  @Test
//  @WithMockUser
//  void viewBuildingByCategory() throws Exception {
//    // given (Mock 데이터 설정)
//    List<BuildingResponse> mockBuildings = List.of(
//            new BuildingResponse(16L, "공학관", 21L, "공",
//                    BigDecimal.valueOf(37.5418220000000), BigDecimal.valueOf(127.0788450000000)),
//            new BuildingResponse(1L, "경영관", 2L, "경영",
//                    BigDecimal.valueOf(37.5444190000000), BigDecimal.valueOf(127.0763700000000))
//    );
//    given(buildingSearchService.viewBuildingByCategory("레스티오")).willReturn(mockBuildings);
//
//
//    //when then
//    mockMvc.perform(get("/api/v1/buildings/view")
//                    .param("category", "레스티오")
//                    .contentType(MediaType.APPLICATION_JSON)
//            )
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.code").value("200"))
//            .andExpect(jsonPath("$.status").value("OK"))
//            .andExpect(jsonPath("$.message").value("OK"))
//            .andExpect(jsonPath("$.data[0].buildingId").value(16))
//            .andExpect(jsonPath("$.data[0].buildingName").value("공학관"))
//            .andExpect(jsonPath("$.data[0].buildingNumber").value(21))
//            .andExpect(jsonPath("$.data[0].buildingAbbreviation").value("공"))
//            .andExpect(jsonPath("$.data[0].latitude").value(37.541822))
//            .andExpect(jsonPath("$.data[0].longitude").value(127.078845))
//            .andExpect(jsonPath("$.data[1].buildingId").value(1))
//            .andExpect(jsonPath("$.data[1].buildingName").value("경영관"))
//            .andExpect(jsonPath("$.data[1].buildingNumber").value(2))
//            .andExpect(jsonPath("$.data[1].buildingAbbreviation").value("경영"))
//            .andExpect(jsonPath("$.data[1].latitude").value(37.544419))
//            .andExpect(jsonPath("$.data[1].longitude").value(127.076370))
//
//            .andDo(restDocs.document(
//                    resource(
//                            ResourceSnippetParameters.builder()
//                                    .tag("빌딩 API")
//                                    .description("특정 카테고리명으로 건물정보 검색")
//                                    .responseFields(
//                            fieldWithPath("code")
//                                    .type(JsonType.NUMBER)
//                                    .description("성공시 반환 코드 (200)"),
//                            fieldWithPath("status")
//                                    .type(JsonType.STRING)
//                                    .description("올바른 인증코드 시 상태 값 (OK)"),
//                            fieldWithPath("message")
//                                    .type(JsonType.STRING)
//                                    .description("올바른 인증코드 시 메시지 (OK)"),
//                            fieldWithPath("data[].buildingId")
//                                    .type(JsonType.NUMBER).description("빌딩 ID"),
//                            fieldWithPath("data[].buildingName")
//                                    .type(JsonType.STRING)
//                                    .description("빌딩 이름"),
//                            fieldWithPath("data[].buildingNumber")
//                                    .type(JsonType.NUMBER)
//                                    .description("빌딩 번호"),
//                            fieldWithPath("data[].buildingAbbreviation")
//                                    .type(JsonType.STRING)
//                                    .description("빌딩 약어"),
//                            fieldWithPath("data[].latitude")
//                                    .type(JsonType.NUMBER)
//                                    .description("위도"),
//                            fieldWithPath("data[].longitude")
//                                    .type(JsonType.NUMBER)
//                                    .description("경도")
//                    ).build())));
//  }

  @DisplayName("특정 카테고리명과 건물번호로 해당 카테고리의 디테일 정보를 출력한다.")
  @Nested
  class CategoryDetailInfo {

    @DisplayName("카테고리명에 따라서 분류")
    @Nested
    class CategoryIs {

      @DisplayName("1) 카테고리명이 '학생 식당'인 경우")
      @Test
      @WithMockUser
      void viewBuildingByCategoryInBuilding_STUDENT_CAFETERIA() throws Exception {
        // given (Mock 데이터 설정)
        BuildindgCategoryRequest request = new BuildindgCategoryRequest("학생 식당", 21L);
        String content = objectMapper.writeValueAsString(request);

        List<MenuSimpleResponse> menuList = List.of(
                new MenuSimpleResponse("마라탕", 4000L, "마라탕url"),
                new MenuSimpleResponse("쌀국수", 4500L, "쌀국수url"),
                new MenuSimpleResponse("순대", 3000L, "순대url"),
                new MenuSimpleResponse("소금삼겹", 2500L, "소금삼겹url")
        );

        CategoryDetailResponse<MenuSimpleResponse> detailData = CategoryDetailResponse.<MenuSimpleResponse>builder()
                .category("학생 식당")
                .floor(-1L)
                .detailList(menuList)
                .build();

        given(buildingSearchService
                .viewBuildingDetailByCategory("학생 식당", 21L))
                .willReturn(detailData);

        //when then
        mockMvc.perform(post("/api/v1/buildings/view")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data.category").value("학생 식당"))
                .andExpect(jsonPath("$.data.floor").value(-1))
                .andExpect(jsonPath("$.data.detailList[0].name").value("마라탕"))
                .andExpect(jsonPath("$.data.detailList[0].price").value(4000))
                .andExpect(jsonPath("$.data.detailList[0].imageUrl").value("마라탕url"))
                .andExpect(jsonPath("$.data.detailList[1].name").value("쌀국수"))
                .andExpect(jsonPath("$.data.detailList[1].price").value(4500))
                .andExpect(jsonPath("$.data.detailList[1].imageUrl").value("쌀국수url")).andExpect(jsonPath("$.data.detailList[1].name").value("쌀국수"))
                .andExpect(jsonPath("$.data.detailList[2].name").value("순대"))
                .andExpect(jsonPath("$.data.detailList[2].price").value(3000))
                .andExpect(jsonPath("$.data.detailList[2].imageUrl").value("순대url"))
                .andExpect(jsonPath("$.data.detailList[3].name").value("소금삼겹"))
                .andExpect(jsonPath("$.data.detailList[3].price").value(2500))
                .andExpect(jsonPath("$.data.detailList[3].imageUrl").value("소금삼겹url"))

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("카테고리(학생식당)로 검색")
                                        .responseFields(
                                fieldWithPath("code").type(JsonType.NUMBER)
                                        .description("성공시 반환 코드 (200)"),
                                fieldWithPath("status").type(JsonType.STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(JsonType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.category").type(JsonType.STRING)
                                        .description("카테고리명"),
                                fieldWithPath("data.floor").type(JsonType.NUMBER)
                                        .description("해당 층 (지하 -1)"),
                                fieldWithPath("data.detailList[].name").type(JsonType.STRING)
                                        .description("메뉴 이름"),
                                fieldWithPath("data.detailList[].price").type(JsonType.NUMBER)
                                        .description("메뉴 가격"),
                                fieldWithPath("data.detailList[].imageUrl").type(JsonType.STRING)
                                        .description("메뉴 이미지 URL")
                        ).build()
                )));
      }


      @DisplayName("2) 카테고리명이 '케이큐브'인 경우")
      @Test
      @WithMockUser
      void viewBuildingByCategoryInBuilding_KCUBE() throws Exception {
        // given (Mock 데이터 설정)
        BuildindgCategoryRequest request = new BuildindgCategoryRequest("케이큐브", 1L);
        String content = objectMapper.writeValueAsString(request);

        CategoryDetailResponse<MenuSimpleResponse> detailData = CategoryDetailResponse.<MenuSimpleResponse>builder()
                .category("케이큐브")
                .floor(1L)
                .detailList(List.of())
                .build();

        given(buildingSearchService
                .viewBuildingDetailByCategory("케이큐브", 1L))
                .willReturn(detailData);

        //when then
        mockMvc.perform(post("/api/v1/buildings/view")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data.category").value("케이큐브"))
                .andExpect(jsonPath("$.data.floor").value(1))
                .andExpect(jsonPath("$.data.detailList").isEmpty())

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("케이큐브로 검색")
                                        .responseFields(
                                fieldWithPath("code").type(JsonType.NUMBER)
                                        .description("성공시 반환 코드 (200)"),
                                fieldWithPath("status").type(JsonType.STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(JsonType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.category").type(JsonType.STRING)
                                        .description("카테고리명"),
                                fieldWithPath("data.floor").type(JsonType.NUMBER)
                                        .description("해당 층 (지하 -1)"),
                                fieldWithPath("data.detailList").type(JsonType.NULL)
                                        .description("해당 카테고리에 대한 상세 정보 (없을 경우 빈 배열")

                        ).build()
                )));
      }

      @DisplayName("3) 카테고리명이 '씨유편의점'인 경우")
      @Test
      @WithMockUser
      void viewBuildingByCategoryInBuilding_CU() throws Exception {
        // given
        BuildindgCategoryRequest request = new BuildindgCategoryRequest("씨유편의점", 3L);
        String content = objectMapper.writeValueAsString(request);

        List<MenuSimpleResponse> menuList = List.of(
                new MenuSimpleResponse("삼각김밥", 1200L, "삼각김밥url")

        );

        CategoryDetailResponse<MenuSimpleResponse> detailData = CategoryDetailResponse.<MenuSimpleResponse>builder()
                .category("씨유편의점")
                .floor(1L)
                .detailList(menuList)
                .build();

        given(buildingSearchService.viewBuildingDetailByCategory("씨유편의점", 3L))
                .willReturn(detailData);

        // when then
        mockMvc.perform(post("/api/v1/buildings/view")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data.category").value("씨유편의점"))
                .andExpect(jsonPath("$.data.floor").value(1))
                .andExpect(jsonPath("$.data.detailList[0].name").value("삼각김밥"))
                .andExpect(jsonPath("$.data.detailList[0].price").value(1200))
                .andExpect(jsonPath("$.data.detailList[0].imageUrl").value("삼각김밥url"))

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("카테고리(씨유)로 검색")
                                        .responseFields(
                                fieldWithPath("code").type(JsonType.NUMBER).description("성공시 반환 코드 (200)"),
                                fieldWithPath("status").type(JsonType.STRING).description("응답 상태"),
                                fieldWithPath("message").type(JsonType.STRING).description("응답 메시지"),
                                fieldWithPath("data.category").type(JsonType.STRING).description("카테고리명"),
                                fieldWithPath("data.floor").type(JsonType.NUMBER).description("해당 층"),
                                fieldWithPath("data.detailList[].name").type(JsonType.STRING).description("상품명"),
                                fieldWithPath("data.detailList[].price").type(JsonType.NUMBER).description("상품 가격"),
                                fieldWithPath("data.detailList[].imageUrl").type(JsonType.STRING).description("상품 이미지 URL")
                        ).build()
                )));
      }

      @DisplayName("4) 카테고리명이 '레스티오'인 경우")
      @Test
      @WithMockUser
      void viewBuildingByCategoryInBuilding_RESTIO() throws Exception {
        // given
        BuildindgCategoryRequest request = new BuildindgCategoryRequest("레스티오", 4L);
        String content = objectMapper.writeValueAsString(request);

        List<MenuSimpleResponse> menuList = List.of(
                new MenuSimpleResponse("파스타", 9500L, "파스타url"),
                new MenuSimpleResponse("스테이크", 20000L, "스테이크url"),
                new MenuSimpleResponse("리조또", 13000L, "리조또url"),
                new MenuSimpleResponse("샐러드", 8000L, "샐러드url")
        );

        CategoryDetailResponse<MenuSimpleResponse> detailData = CategoryDetailResponse.<MenuSimpleResponse>builder()
                .category("레스티오")
                .floor(2L)
                .detailList(menuList)
                .build();

        given(buildingSearchService.viewBuildingDetailByCategory("레스티오", 4L))
                .willReturn(detailData);

        // when then
        mockMvc.perform(post("/api/v1/buildings/view")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data.category").value("레스티오"))
                .andExpect(jsonPath("$.data.floor").value(2))
                .andExpect(jsonPath("$.data.detailList[0].name").value("파스타"))
                .andExpect(jsonPath("$.data.detailList[0].price").value(9500))
                .andExpect(jsonPath("$.data.detailList[0].imageUrl").value("파스타url"))

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("레스티오로 검색")
                                        .responseFields(
                                fieldWithPath("code").type(JsonType.NUMBER).description("성공시 반환 코드 (200)"),
                                fieldWithPath("status").type(JsonType.STRING).description("응답 상태"),
                                fieldWithPath("message").type(JsonType.STRING).description("응답 메시지"),
                                fieldWithPath("data.category").type(JsonType.STRING).description("카테고리명"),
                                fieldWithPath("data.floor").type(JsonType.NUMBER).description("해당 층"),
                                fieldWithPath("data.detailList[].name").type(JsonType.STRING).description("메뉴 이름"),
                                fieldWithPath("data.detailList[].price").type(JsonType.NUMBER).description("메뉴 가격"),
                                fieldWithPath("data.detailList[].imageUrl").type(JsonType.STRING).description("메뉴 이미지 URL")
                        ).build()
                )));
      }

      @DisplayName("5) 카테고리명이 '1894카페'인 경우")
      @Test
      @WithMockUser
      void viewBuildingByCategoryInBuilding_CAFE_1894() throws Exception {
        // given
        BuildindgCategoryRequest request = new BuildindgCategoryRequest("1894카페", 5L);
        String content = objectMapper.writeValueAsString(request);

        List<MenuSimpleResponse> menuList = List.of(
                new MenuSimpleResponse("아메리카노", 3000L, "아메리카노url"),
                new MenuSimpleResponse("카페라떼", 3500L, "카페라떼url"),
                new MenuSimpleResponse("카푸치노", 4000L, "카푸치노url"),
                new MenuSimpleResponse("초코라떼", 4500L, "초코라떼url")
        );

        CategoryDetailResponse<MenuSimpleResponse> detailData = CategoryDetailResponse.<MenuSimpleResponse>builder()
                .category("1894카페")
                .floor(1L)
                .detailList(menuList)
                .build();

        given(buildingSearchService.viewBuildingDetailByCategory("1894카페", 5L))
                .willReturn(detailData);

        // when then
        mockMvc.perform(post("/api/v1/buildings/view")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))

                .andExpect(jsonPath("$.data.category").value("1894카페"))
                .andExpect(jsonPath("$.data.floor").value(1))
                .andExpect(jsonPath("$.data.detailList[0].name").value("아메리카노"))
                .andExpect(jsonPath("$.data.detailList[0].price").value(3000))
                .andExpect(jsonPath("$.data.detailList[0].imageUrl").value("아메리카노url"))

                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("빌딩 API")
                                        .description("1894로 검색")
                                        .responseFields(
                                fieldWithPath("code").type(JsonType.NUMBER).description("성공시 반환 코드 (200)"),
                                fieldWithPath("status").type(JsonType.STRING).description("응답 상태"),
                                fieldWithPath("message").type(JsonType.STRING).description("응답 메시지"),
                                fieldWithPath("data.category").type(JsonType.STRING).description("카테고리명"),
                                fieldWithPath("data.floor").type(JsonType.NUMBER).description("해당 층"),
                                fieldWithPath("data.detailList[].name").type(JsonType.STRING).description("메뉴 이름"),
                                fieldWithPath("data.detailList[].price").type(JsonType.NUMBER).description("메뉴 가격"),
                                fieldWithPath("data.detailList[].imageUrl").type(JsonType.STRING).description("메뉴 이미지 URL")
                        ).build()
                )));
      }

    }
  }

//  @DisplayName("검색어를 포함한 모든 건물정보를 출력한다.")
//  @Test
//  @WithMockUser
//  void viewAvailableTextNameList() throws Exception {
//    // given (Mock 데이터 설정)
//    List<BuildingResponse> mockBuildings = List.of(
//            new BuildingResponse(16L, "공학관", 21L, "공",
//                    BigDecimal.valueOf(37.5418220000000), BigDecimal.valueOf(127.0788450000000)),
//            new BuildingResponse(1L, "신공학관", 2L, "신공",
//                    BigDecimal.valueOf(37.5444190000000), BigDecimal.valueOf(127.0763700000000))
//    );
//    given(buildingSearchService.searchAvailableText("공학")).willReturn(mockBuildings);
//
//    //when then
//    mockMvc.perform(get("/api/v1/buildings/text")
//                    .contentType(MediaType.APPLICATION_JSON)
//            )
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.code").value("200"))
//            .andExpect(jsonPath("$.status").value("OK"))
//            .andExpect(jsonPath("$.message").value("OK"))
//
//            .andExpect(jsonPath("$.data[0].buildingId").value(16))
//            .andExpect(jsonPath("$.data[0].buildingName").value("공학관"))
//            .andExpect(jsonPath("$.data[0].buildingNumber").value(21))
//            .andExpect(jsonPath("$.data[0].buildingAbbreviation").value("공"))
//            .andExpect(jsonPath("$.data[0].latitude").value(37.541822))
//            .andExpect(jsonPath("$.data[0].longitude").value(127.078845))
//            .andExpect(jsonPath("$.data[1].buildingId").value(1))
//            .andExpect(jsonPath("$.data[1].buildingName").value("신공학관"))
//            .andExpect(jsonPath("$.data[1].buildingNumber").value(2))
//            .andExpect(jsonPath("$.data[1].buildingAbbreviation").value("신공"))
//            .andExpect(jsonPath("$.data[1].latitude").value(37.544419))
//            .andExpect(jsonPath("$.data[1].longitude").value(127.076370))
//
//            .andDo(restDocs.document(
//                    responseFields(
//                            fieldWithPath("code")
//                                    .type(JsonType.NUMBER)
//                                    .description("성공시 반환 코드 (200)"),
//                            fieldWithPath("status")
//                                    .type(JsonType.STRING)
//                                    .description("올바른 인증코드 시 상태 값 (OK)"),
//                            fieldWithPath("message")
//                                    .type(JsonType.STRING)
//                                    .description("올바른 인증코드 시 메시지 (OK)"),
//                            fieldWithPath("data[].buildingId")
//                                    .type(JsonType.NUMBER).description("빌딩 ID"),
//                            fieldWithPath("data[].buildingName")
//                                    .type(JsonType.STRING)
//                                    .description("빌딩 이름"),
//                            fieldWithPath("data[].buildingNumber")
//                                    .type(JsonType.NUMBER)
//                                    .description("빌딩 번호"),
//                            fieldWithPath("data[].buildingAbbreviation")
//                                    .type(JsonType.STRING)
//                                    .description("빌딩 약어"),
//                            fieldWithPath("data[].latitude")
//                                    .type(JsonType.NUMBER)
//                                    .description("위도"),
//                            fieldWithPath("data[].longitude")
//                                    .type(JsonType.NUMBER)
//                                    .description("경도")
//                    )));
//  }
}