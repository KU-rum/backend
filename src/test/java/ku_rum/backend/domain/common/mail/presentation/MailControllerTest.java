package ku_rum.backend.domain.common.mail.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsUnitTestSupport;
import ku_rum.backend.domain.common.mail.application.MailService;
import ku_rum.backend.domain.common.mail.dto.request.MailSendRequest;
import ku_rum.backend.domain.common.mail.dto.request.MailVerificationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(MailController.class)
@ActiveProfiles("test")
class MailControllerTest extends RestDocsUnitTestSupport {

    @MockBean
    private MailService mailService;

    @DisplayName("이메일 인증 요청을 보낸다.")
    @Test
    @WithMockUser
    void authCode() throws Exception {
        //given
        MailSendRequest mailSendRequest = new MailSendRequest("kmw10693@naver.com");

        //when then
        mockMvc.perform(post("/api/v1/mails/auth-codes")
                        .content(objectMapper.writeValueAsString(mailSendRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value("메일이 성공적으로 전송되었습니다."))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("메일 관련 API")
                                        .description("이메일 인증 요청")
                                        .requestFields(
                                                fieldWithPath("email")
                                                        .type(JsonFieldType.STRING)
                                                        .description("건국대학교 웹메일")
                                                        .attributes(constraints("이메일 형식을 지켜야 합니다."))
                                        )
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonFieldType.STRING)
                                                        .description("성공시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("성공 시 메시지 (OK)"),
                                                fieldWithPath("data")
                                                        .type(JsonFieldType.STRING)
                                                        .description("성공 시 '메일이 성공적으로 전송되었습니다.' 반환합니다.")
                                        ).build())));
    }

    @DisplayName("이메일 인증 코드를 검증한다.")
    @Test
    @WithMockUser
    void verificationCode() throws Exception {
        //given
        MailVerificationRequest mailVerificationRequest = new MailVerificationRequest("kmw10693@naver.com", "123456");

        //when then
        mockMvc.perform(post("/api/v1/mails/verification_codes")
                        .content(objectMapper.writeValueAsString(mailVerificationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.verified").value(true))
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("메일 관련 API")
                                        .description("이메일 인증 코드 검증")
                                        .requestFields(
                                                fieldWithPath("email")
                                                        .type(JsonFieldType.STRING)
                                                        .description("건국대학교 웹메일")
                                                        .attributes(constraints("이메일 형식을 지켜야 합니다.")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("건국대학교 웹메일")
                                                        .attributes(constraints("사용자가 받은 인증코드를 입력받습니다."))
                                        )
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonFieldType.STRING)
                                                        .description("올바른 인증코드 시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("올바른 인증코드 시 메시지 (OK)"),
                                                fieldWithPath("data.verified")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("인증 성공 여부")
                                        ).build())));
    }
}