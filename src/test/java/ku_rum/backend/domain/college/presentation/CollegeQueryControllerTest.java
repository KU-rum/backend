package ku_rum.backend.domain.college.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import ku_rum.backend.config.RestDocsUnitTestSupport;
import ku_rum.backend.domain.college.application.CollegeQueryService;
import ku_rum.backend.domain.college.dto.response.CollegeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(CollegeQueryController.class)
@ActiveProfiles("test")
class CollegeQueryControllerTest extends RestDocsUnitTestSupport {

    @MockBean
    private CollegeQueryService collegeQueryService;

    @MockBean
    private ku_rum.backend.domain.common.s3.application.S3Service s3Service;

    @Test
    @DisplayName("전체 컬리지 목록 조회 성공")
    @WithMockUser
    void getCollege_success() throws Exception {
        // Given
        List<String> collegeNames = List.of("Arts", "Science", "Engineering");
        CollegeResponse responseDto = new CollegeResponse(collegeNames);
        given(collegeQueryService.findAll()).willReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/v1/colleges")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.name[0]").value("Arts"))
                .andExpect(jsonPath("$.data.name[1]").value("Science"))
                .andExpect(jsonPath("$.data.name[2]").value("Engineering"))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("학과 관련 API")
                                        .description("전체 단과대 목록 조회")
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

                                        )
                                        .build()
                        )
                ));
    }
}