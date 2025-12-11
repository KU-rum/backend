package ku_rum.backend.domain.user.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.alarm.application.AlarmService;
import ku_rum.backend.domain.alarm.application.FcmService;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.application.UserValidator;
import ku_rum.backend.domain.user.domain.DeviceType;
import ku_rum.backend.domain.user.dto.request.UserFcmRequest;
import ku_rum.backend.domain.user.dto.response.UserFcmResponse;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserFcmControllerTest extends RestDocsTestSupport {

    @MockBean
    AlarmService alarmService;

    @MockBean
    UserService userService;

    @MockBean
    UserValidator userValidator;

    @MockBean
    FcmService fcmService;

    @MockBean
    ApiLogRepository apiLogRepository;

    @MockBean
    SecurityFilterChain securityFilterChain;

    @MockBean
    JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @DisplayName("사용자 fcm 토큰을 저장한다")
    @Test
    void saveFcmToken() throws Exception {

        // given
        String token = "fcm 토큰";
        DeviceType deviceType = DeviceType.ANDROID;
        UserFcmRequest request = new UserFcmRequest(token, deviceType);
        UserFcmResponse response = new UserFcmResponse(1L, token, deviceType);

        when(fcmService.createFcmToken(any(), eq(request)))
                .thenReturn(response);

        // when
        mockMvc.perform(post("/api/v1/users/fcm")
                        .header("Authorization", "Bearer test-access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "token": "fcm 토큰",
                                  "deviceType": "ANDROID"
                                }
                                """))
                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("알림 FCM API")
                                .description("사용자의 FCM 토큰을 저장한다")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰입니다.")
                                )
                                .requestFields(
                                        fieldWithPath("token").description("토큰"),
                                        fieldWithPath("deviceType").description("기기 종류(ANDROID,IOS)")
                                )
                                .responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data.id").description("id"),
                                        fieldWithPath("data.token").description("토큰"),
                                        fieldWithPath("data.deviceType").description("기기 종류")
                                )
                                .build()
                )));
    }
}
