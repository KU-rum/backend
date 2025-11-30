package ku_rum.backend.domain.user.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.application.UserValidator;
import ku_rum.backend.domain.user.dto.request.TemporaryUserRequest;
import ku_rum.backend.domain.user.dto.response.TemporaryUserResponse;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(TemporaryUserController.class)
@ActiveProfiles("test")
public class TemporaryUserControllerTest extends RestDocsTestSupport {

    @MockBean
    UserService userService;

    @MockBean
    UserValidator userValidator;

    @MockBean
    private ApiLogRepository apiLogRepository;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @DisplayName("유저 토큰을 생성한다")
    @Test
    void createToken() throws Exception {
        //given
        TemporaryUserRequest request = new TemporaryUserRequest(1L);
        TemporaryUserResponse response = new TemporaryUserResponse("accessToken", "refreshToken", 1, 2, true);

        given(userService.getUserToken(eq(request)))
                .willReturn(response);

        //when & then
        mockMvc.perform(post("/api/v1/user/temporary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("""
                                {
                                  "userId": 1
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("맴버 테스트 API")
                                .description("회원 토큰을 발급한다.")
                                .requestFields(
                                        fieldWithPath("userId").description("유저 ID입니다.")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data.accessToken").description("엑세스 토큰"),
                                        fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                                        fieldWithPath("data.accessExpireIn").description("엑세스 토큰 만료 시간"),
                                        fieldWithPath("data.refreshExpireIn").description("리프레시 토큰 만료 시간"),
                                        fieldWithPath("data.isFirstLogin").description("최초 로그인 여부")
                                )
                                .build()
                )));
    }
}
