package ku_rum.backend.domain.common.image.presentation;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.common.image.application.ImageStorageService;
import ku_rum.backend.domain.common.image.dto.response.ImageResponse;
import ku_rum.backend.global.batch.BatchScheduler;
import ku_rum.backend.global.config.S3Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.json.JsonType.STRING;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class ImageControllerTest extends RestDocsTestSupport {

    @MockBean
    private ImageStorageService imageStorageService;

    @MockBean
    private S3Config s3Config;

    @MockBean
    private BatchScheduler batchScheduler;

    @DisplayName("Presigned URL 조회 API")
    @Test
    @WithMockUser
    void getPresignedUrl() throws Exception {
        // given
        String fileName = "test-image.png";
        String presignedUrl = "https://s3.amazonaws.com/bucket-name/images/test-image.png";
        String imageUrl = "https://s3.amazonaws.com/bucket-name/images/test-image.png";
        ImageResponse response = ImageResponse.from(imageUrl, presignedUrl);

        when(imageStorageService.getPresignedUrl(fileName)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/images/{fileName}", fileName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.preSignedUrl").value(presignedUrl))
                .andExpect(jsonPath("$.data.imageUrl").value(imageUrl))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("이미지 API")
                                        .description("S3 Presigned/ImageUrl 조회 API")
                                        .pathParameters(
                                                parameterWithName("fileName").description("파일 확장자")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(STRING).description("응답 코드"),
                                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                                fieldWithPath("data.preSignedUrl").type(STRING).description("S3 Presigned URL"),
                                                fieldWithPath("data.imageUrl").type(STRING).description("조회용 이미지 URL")
                                        )
                                        .build()
                        )
                ));
    }
}