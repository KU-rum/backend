package ku_rum.backend.domain.banner.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.List;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.banner.application.BannerService;
import ku_rum.backend.domain.banner.dto.BannerCreateRequest;
import ku_rum.backend.domain.banner.dto.BannerResponse;
import ku_rum.backend.domain.banner.dto.BannerUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BannerControllerTest extends RestDocsTestSupport {

    @MockBean
    private BannerService bannerService;


    @Test
    @DisplayName("배너 생성")
    @WithMockUser
    void createBanner() throws Exception {

        BannerCreateRequest request = new BannerCreateRequest(
                "이벤트 배너",
                "https://cdn.kuroom.shop/banner.png",
                "https://event.kuroom.shop",
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );

        mockMvc.perform(post("/admin/banners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("배너 관리 API")
                                .description("배너 생성")
                                .requestFields(
                                        fieldWithPath("title")
                                                .type(JsonFieldType.STRING)
                                                .description("배너 제목"),
                                        fieldWithPath("imageUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("배너 이미지 URL"),
                                        fieldWithPath("linkUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("클릭 시 이동 URL"),
                                        fieldWithPath("displayOrder")
                                                .type(JsonFieldType.NUMBER)
                                                .description("노출 순서 (1~4)"),
                                        fieldWithPath("startAt")
                                                .type(JsonFieldType.STRING)
                                                .description("노출 시작 시각"),
                                        fieldWithPath("endAt")
                                                .type(JsonFieldType.STRING)
                                                .description("노출 종료 시각")
                                )
                                .build()
                )));
    }

    @Test
    @DisplayName("배너 수정")
    @WithMockUser
    void updateBanner() throws Exception {

        BannerUpdateRequest request = new BannerUpdateRequest(
                "수정된 배너",
                "https://cdn.kuroom.shop/banner2.png",
                "https://event2.kuroom.shop",
                2,
                true,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3)
        );

        mockMvc.perform(put("/admin/banners/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("배너 관리 API")
                                .description("배너 수정")
                                .requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("배너 제목"),
                                        fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("배너 이미지 URL"),
                                        fieldWithPath("linkUrl").type(JsonFieldType.STRING).description("클릭 URL"),
                                        fieldWithPath("displayOrder").type(JsonFieldType.NUMBER).description("노출 순서"),
                                        fieldWithPath("active").type(JsonFieldType.BOOLEAN).description("활성 여부"),
                                        fieldWithPath("startAt").type(JsonFieldType.STRING).description("시작 시간"),
                                        fieldWithPath("endAt").type(JsonFieldType.STRING).description("종료 시간")
                                )
                                .build()
                )));
    }

    @Test
    @DisplayName("배너 삭제 (비활성화)")
    @WithMockUser
    void deleteBanner() throws Exception {

        mockMvc.perform(delete("/admin/banners/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("배너 관리 API")
                                .description("배너 삭제 (비활성화)")
                                .build()
                )));
    }

    @Test
    @DisplayName("활성 배너 목록 조회")
    void getBanners() throws Exception {

        given(bannerService.getActiveBanners())
                .willReturn(List.of(
                        BannerResponse.builder()
                                .id(1L)
                                .title("이벤트 배너")
                                .imageUrl("https://cdn.kuroom.shop/banner.png")
                                .linkUrl("https://event.kuroom.shop")
                                .build()
                ));

        mockMvc.perform(get("/banners"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("배너 API")
                                .description("프론트용 배너 목록 조회 (최대 4개)")
                                .responseFields(
                                        fieldWithPath("[].id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("배너 ID"),
                                        fieldWithPath("[].title")
                                                .type(JsonFieldType.STRING)
                                                .description("배너 제목"),
                                        fieldWithPath("[].imageUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("배너 이미지 URL"),
                                        fieldWithPath("[].linkUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("클릭 이동 URL")
                                )
                                .build()
                )));
    }
}
