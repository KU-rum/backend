package ku_rum.backend.domain.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import ku_rum.backend.domain.user.application.mail.MailService;
import ku_rum.backend.domain.user.dto.request.mail.MailSendRequest;
import ku_rum.backend.domain.user.dto.request.mail.MailVerificationRequest;
import ku_rum.backend.domain.user.presentation.mail.MailController;
import ku_rum.backend.global.config.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailController.class)
@Import(SecurityConfig.class)
class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MailService mailService;

    @DisplayName("이메일 인증 요청을 보낸다.")
    @Test
    void sendMail() throws Exception {
        //given
        MailSendRequest mailSendRequest = new MailSendRequest("kmw10693@konkuk.ac.kr");

        //when then
        mockMvc.perform(post("/api/v1/mails")
                        .content(objectMapper.writeValueAsString(mailSendRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("이메일 인증 코드를 검증한다.")
    @Test
    void verificationEmail() throws Exception {
        //given
        MailVerificationRequest mailVerificationRequest = new MailVerificationRequest("kmw10693@konkuk.ac.kr", "1234");

        //when then
        mockMvc.perform(get("/api/v1/mails/mail_verifications")
                        .content(objectMapper.writeValueAsString(mailVerificationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }
}