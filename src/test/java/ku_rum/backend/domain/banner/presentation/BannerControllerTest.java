package ku_rum.backend.domain.banner.presentation;


import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import ku_rum.backend.config.RestDocsUnitTestSupport;
import ku_rum.backend.domain.banner.application.BannerService;
import ku_rum.backend.domain.banner.dto.GetBannerResponse;
import ku_rum.backend.domain.banner.dto.PatchBannerRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(BannerController.class)
@ActiveProfiles("test")
public class BannerControllerTest extends RestDocsUnitTestSupport {

    @MockBean
    BannerService bannerService;

    @DisplayName("배너들을 조회한다")
    @Test
    void getBanners() throws Exception {
        //given
        Long bannerId = 1L;
        String bannerImageUrl = "url";
        String bannerLink = "url";
        List<GetBannerResponse> response = List.of(new GetBannerResponse(bannerId, bannerImageUrl, bannerLink));

        given(bannerService.findBanners())
                .willReturn(response);

        //when
        mockMvc.perform(get("/api/v1/banner")
                        .header("Authorization", "Bearer access-token"))
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("배너 관련 API")
                                .description("배너 리스트 조회")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data[].bannerId").type(JsonFieldType.NUMBER)
                                                .description("배너 ID"),
                                        fieldWithPath("data[].bannerImageUrl").type(JsonFieldType.STRING)
                                                .description("배너 이미지 URL"),
                                        fieldWithPath("data[].bannerLink").type(JsonFieldType.STRING)
                                                .description("배너 링크 URL")
                                )
                                .build())));
    }

    @DisplayName("배너를 추가한다")
    @Test
    void addBanners() throws Exception {
        // given
        MockMultipartFile image1 = new MockMultipartFile(
                "images", "banner1.png", MediaType.IMAGE_PNG_VALUE, "image1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile(
                "images", "banner2.png", MediaType.IMAGE_PNG_VALUE, "image2".getBytes());

        doNothing().when(bannerService).addBanner(any(PatchBannerRequests.class));

        // when & then
        mockMvc.perform(multipart("/api/v1/banner")
                        .file(image1)
                        .file(image2)
                        .param("links", "https://example.com/1", "https://example.com/2")
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .header("Authorization", "Bearer access-token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("배너 관련 API")
                                .description("배너 추가")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지")
                                )
                                .build())));
    }

    @DisplayName("배너를 삭제한다")
    @Test
    void deleteBanner() throws Exception {
        // given
        Long bannerId = 1L;
        doNothing().when(bannerService).deleteById(bannerId);

        // when & then
        mockMvc.perform(delete("/api/v1/banner/{bannerId}", bannerId)
                        .header("Authorization", "Bearer access-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("배너 관련 API")
                                .description("배너 삭제")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .pathParameters(
                                        parameterWithName("bannerId").description("삭제할 배너 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지")
                                )
                                .build())));
    }
}
