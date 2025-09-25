package ku_rum.backend.domain.auth.presentation;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.auth.application.AuthService;
import ku_rum.backend.domain.auth.dto.request.LoginRequest;
import ku_rum.backend.domain.auth.dto.request.ReissueRequest;
import ku_rum.backend.domain.auth.dto.response.AuthResponse;
import ku_rum.backend.domain.department.dto.DepartmentResponse;
import ku_rum.backend.domain.user.dto.response.TokenResponse;
import ku_rum.backend.domain.user.dto.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.json.JsonType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AuthControllerTest extends RestDocsTestSupport {

    @MockBean
    private AuthService authService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @DisplayName("로그인을 진행한다.")
    @Test
    @WithMockUser
    void login() throws Exception {
        // given
        LoginRequest request = new LoginRequest("kmw10693", "testtest");

        // TokenResponse 설정
        TokenResponse tokenResponse = TokenResponse.of("accessToken", "refreshToken", 1800000L, 604800000L, false);
        List<DepartmentResponse> departmentResponses = List.of();
        // UserResponse 설정 (사용자 정보도 포함해야 하므로, 예시로 넣음)
        UserResponse userResponse = UserResponse.of(
                1L, "oauthId", "kmw10693", "email@example.com", "nickname", "studentId", "imageUrl"
        , departmentResponses);

        // AuthResponse 설정
        AuthResponse authResponse = AuthResponse.of(tokenResponse, userResponse);

        // authService.login을 목 설정
        Mockito.when(authService.login(request)).thenReturn(authResponse);

        // when then
        mockMvc.perform(post("/api/v1/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                // 수정된 JSON path 경로
                .andExpect(jsonPath("data.tokenResponse.accessToken").value("accessToken"))
                .andExpect(jsonPath("data.tokenResponse.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("data.tokenResponse.accessExpireIn").value(1800000L))
                .andExpect(jsonPath("data.tokenResponse.refreshExpireIn").value(604800000L))
                .andExpect(jsonPath("data.userResponse.loginId").value("kmw10693"))
                .andExpect(jsonPath("data.userResponse.nickname").value("nickname"))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("권한 관련 API")
                                        .description("로그인")
                                        .requestFields(
                                                fieldWithPath("loginId")
                                                        .type(JsonType.STRING)
                                                        .description("멤버 아이디")
                                                        .attributes(constraints("아이디 입력은 필수입니다. 최소 6자 이상입니다.")),
                                                fieldWithPath("password")
                                                        .type(JsonType.STRING)
                                                        .description("비밀번호")
                                                        .attributes(constraints("비밀번호 입력은 필수입니다."))
                                        )
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonType.STRING)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonType.STRING)
                                                        .description("성공시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonType.STRING)
                                                        .description("성공 시 메시지 (OK)"),
                                                fieldWithPath("data.tokenResponse.accessToken")
                                                        .type(JsonType.STRING)
                                                        .description("엑세스 토큰"),
                                                fieldWithPath("data.tokenResponse.refreshToken")
                                                        .type(JsonType.STRING)
                                                        .description("리프레시 토큰"),
                                                fieldWithPath("data.tokenResponse.accessExpireIn")
                                                        .type(JsonType.STRING)
                                                        .description("엑세스 토큰 만료 기간"),
                                                fieldWithPath("data.tokenResponse.refreshExpireIn")
                                                        .type(JsonType.STRING)
                                                        .description("리프레시 토큰 만료 기간"),
                                                fieldWithPath("data.userResponse.loginId")
                                                        .type(JsonType.STRING)
                                                        .description("로그인 아이디"),
                                                fieldWithPath("data.userResponse.nickname")
                                                        .type(JsonType.STRING)
                                                        .description("사용자 닉네임"),
                                                fieldWithPath("data.userResponse.email")
                                                        .type(JsonType.STRING)
                                                        .description("사용자 이메일"),
                                                fieldWithPath("data.userResponse.studentId")
                                                        .type(JsonType.STRING)
                                                        .description("사용자 학번"),
                                                fieldWithPath("data.userResponse.imageUrl")
                                                        .type(JsonType.STRING)
                                                        .description("사용자 이미지 URL"),
                                                fieldWithPath("data.userResponse.id")
                                                        .type(JsonType.STRING)
                                                        .description("사용자 인덱스"),
                                                fieldWithPath("data.userResponse.oauthId")
                                                        .type(JsonType.STRING)
                                                        .description("사용자 oauthId"),
                                                fieldWithPath("data.userResponse.departmentResponse")
                                                        .type(JsonType.STRING)
                                                        .description("사용자 학과"),
                                                fieldWithPath("data.tokenResponse.isFirstLogin")
                                                        .type(JsonType.BOOLEAN)
                                                        .description("사용자 최초 로그인 여부")
                                        ).build())));
    }


    @DisplayName("로그아웃을 진행한다.")
    @Test
    @WithMockUser
    void logout() throws Exception {
        // when then
        mockMvc.perform(patch("/api/v1/auth/logout")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpGJdOigSKjxMIab0cV06xFjSpwrq70")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("권한 관련 API")
                                        .description("로그아웃")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다. Authorization 헤더에 넣어주세요.")
                                        )
                                        .responseFields(

                                                fieldWithPath("code")
                                                        .type(JsonType.STRING)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonType.STRING)
                                                        .description("성공시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonType.STRING)
                                                        .description("성공 시 메시지 (OK)"),
                                                fieldWithPath("data")
                                                        .type(JsonType.STRING)
                                                        .description("성공 시 반환 메시지")
                                        ).build())));
    }

    @DisplayName("토큰을 재발급한다")
    @Test
    @WithMockUser
    void reissue() throws Exception {
        ReissueRequest request = new ReissueRequest("refreshToken");
        TokenResponse tokenResponse = TokenResponse.of("accessToken", "refreshToken", 1800000L, 604800000L, false);
        Mockito.when(authService.reissue(request)).thenReturn(tokenResponse);

        // when then
        mockMvc.perform(patch("/api/v1/auth/reissue")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("권한 관련 API")
                                        .description("토큰 재발급")
                                        .requestFields(
                                                fieldWithPath("refreshToken")
                                                        .type(JsonType.STRING)
                                                        .description("발급받은 리프레시 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonType.STRING)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonType.STRING)
                                                        .description("성공시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonType.STRING)
                                                        .description("성공 시 메시지 (OK)"),
                                                fieldWithPath("data.accessToken")
                                                        .type(JsonType.STRING)
                                                        .description("엑세스 토큰"),
                                                fieldWithPath("data.refreshToken")
                                                        .type(JsonType.STRING)
                                                        .description("리프레시 토큰"),
                                                fieldWithPath("data.accessExpireIn")
                                                        .type(JsonType.STRING)
                                                        .description("엑세스 토큰 만료 기간"),
                                                fieldWithPath("data.refreshExpireIn")
                                                        .type(JsonType.STRING)
                                                        .description("리프레시 토큰 만료 기간"),
                                                fieldWithPath("data.isFirstLogin")
                                                        .type(JsonType.BOOLEAN)
                                                        .description("사용자 최초 로그인 여부")
                                        ).build())));
    }

    @DisplayName("임시 토큰을 엑세스/리프레시 토큰으로 교환한다")
    @Test
    @WithMockUser
    void exchangeToken() throws Exception {
        // given
        String tempToken = "temporary_token_value";
        List<DepartmentResponse> list = new ArrayList<>();
        UserResponse userResponse = new UserResponse(1L, "oauthId", "loginId", "email", "nickname", "studentId", "imageUrl", list);
        TokenResponse tokenResponse = new TokenResponse("accessToken", "refreshToken", 1800000L, 604800000L, false);
        AuthResponse authResponse = new AuthResponse(
                tokenResponse,
                userResponse
        );

        Mockito.when(authService.exchangeToken(tempToken)).thenReturn(authResponse);

        // when & then
        mockMvc.perform(post("/api/v1/auth/token?authCode=" + tempToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.tokenResponse.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.userResponse.id").value(1L))
                .andExpect(jsonPath("$.data.userResponse.oauthId").value("oauthId"))
                .andExpect(jsonPath("$.data.userResponse.loginId").value("loginId"))
                .andExpect(jsonPath("$.data.userResponse.email").value("email"))
                .andExpect(jsonPath("$.data.userResponse.nickname").value("nickname"))
                .andExpect(jsonPath("$.data.userResponse.studentId").value("studentId"))
                .andExpect(jsonPath("$.data.userResponse.imageUrl").value("imageUrl"))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("권한 관련 API")
                                        .description("임시 토큰/엑세스 토큰 교환")
                                        .queryParameters(
                                                parameterWithName("authCode")
                                                        .description("발급된 권한 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status").description("성공시 상태 값 (OK)"),
                                                fieldWithPath("message").description("성공 시 메시지 (OK)"),
                                                fieldWithPath("data.tokenResponse.accessToken").description("엑세스 토큰"),
                                                fieldWithPath("data.tokenResponse.refreshToken").description("리프레시 토큰"),
                                                fieldWithPath("data.tokenResponse.accessExpireIn").description("엑세스 만료 기간"),
                                                fieldWithPath("data.tokenResponse.refreshExpireIn").description("리프레시 만료 기간"),
                                                fieldWithPath("data.userResponse.id").description("사용자 ID"),
                                                fieldWithPath("data.userResponse.oauthId").description("사용자 Oauth ID"),
                                                fieldWithPath("data.userResponse.loginId").description("사용자 로그인 ID"),
                                                fieldWithPath("data.userResponse.email").description("사용자 이메일"),
                                                fieldWithPath("data.userResponse.nickname").description("사용자 닉네임"),
                                                fieldWithPath("data.userResponse.studentId").description("사용자 학생 ID"),
                                                fieldWithPath("data.userResponse.imageUrl").description("사용자 이미지 URL"),
                                                fieldWithPath("data.userResponse.departmentResponse").description("사용자 학과 정보"),
                                                fieldWithPath("data.tokenResponse.isFirstLogin").description("사용자 최초 로그인 여부")

                                        )
                                        .build()
                        )
                ));
    }

}