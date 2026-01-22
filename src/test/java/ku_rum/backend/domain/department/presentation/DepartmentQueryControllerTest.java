package ku_rum.backend.domain.department.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.department.application.DepartmentQueryService;
import ku_rum.backend.domain.department.application.UserDepartmentService;
import ku_rum.backend.domain.department.dto.CollegeDepartmentResponse;
import ku_rum.backend.domain.department.dto.DepartmentUrlResponse;
import ku_rum.backend.domain.department.dto.SearchDepartmentResponse;
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
class DepartmentQueryControllerTest extends RestDocsTestSupport {

    @MockBean
    private DepartmentQueryService departmentQueryService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private UserDepartmentService userDepartmentService;

    @Test
    @DisplayName("단일 컬리지명으로 학과 목록 조회 성공")
    @WithMockUser
    void getDepartment_success() throws Exception {
        // Given
        String collegeName = "Engineering";
        List<String> depts = List.of("Mechanical", "Electrical", "Computer Science");
        CollegeDepartmentResponse dto = new CollegeDepartmentResponse(depts);

        given(departmentQueryService.getDepartmentsByCollege(collegeName))
                .willReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/v1/departments")
                        .param("collegeName", collegeName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.name[0]").value("Mechanical"))
                .andExpect(jsonPath("$.data.name[1]").value("Electrical"))
                .andExpect(jsonPath("$.data.name[2]").value("Computer Science"))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("학과 관련 API")
                                        .description("컬리지 이름으로 학과 목록 조회")
                                        .queryParameters(
                                                parameterWithName("collegeName")
                                                        .description("조회할 컬리지 이름")
                                        )
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("응답 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonFieldType.STRING)
                                                        .description("응답 상태 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("응답 메시지"),
                                                fieldWithPath("data.name")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("컬리지 이름 리스트")
                                        ).build()
                        )
                ));
    }

    @Test
    @DisplayName("학과 검색")
    void searchDepartment() throws Exception {
        // given
        List<SearchDepartmentResponse> response = List.of(new SearchDepartmentResponse("응용통계학과", "사회과학대학"));
        given(departmentQueryService.search("응용")).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/departments/search")
                        .param("query", "응용"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data[0].department").value("응용통계학과"))
                .andExpect(jsonPath("$.data[0].college").value("사회과학대학"))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("학과 관련 API")
                                        .description("학과 검색")
                                        .queryParameters(
                                                parameterWithName("query")
                                                        .description("검색어")
                                        )
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("응답 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonFieldType.STRING)
                                                        .description("응답 상태 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("응답 메시지"),
                                                fieldWithPath("data[].department")
                                                        .type(JsonFieldType.STRING)
                                                        .description("학과 이름"),
                                                fieldWithPath("data[].college")
                                                        .type(JsonFieldType.STRING)
                                                        .description("단과대 이름")
                                        ).build()
                        )
                ));
    }

    @Test
    @DisplayName("학과 URL을 확인할 수 있다")
    void getDepartmentResponse() throws Exception {
        // given
        List<DepartmentUrlResponse> response = List.of(new DepartmentUrlResponse("응용통계학과", "url"));
        given(userDepartmentService.getDepartmentUrl()).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/departments/url"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data[0].name").value("응용통계학과"))
                .andExpect(jsonPath("$.data[0].url").value("url"))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("학과 관련 API")
                                        .description("학과 검색")
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("응답 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonFieldType.STRING)
                                                        .description("응답 상태 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("응답 메시지"),
                                                fieldWithPath("data[].name")
                                                        .type(JsonFieldType.STRING)
                                                        .description("학과 이름"),
                                                fieldWithPath("data[].url")
                                                        .type(JsonFieldType.STRING)
                                                        .description("학과 URL")
                                        ).build()
                        )
                ));
    }
}