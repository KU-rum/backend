package ku_rum.backend.domain.user.presentation;

import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.dto.request.mail.EmailValidationRequest;
import ku_rum.backend.domain.user.dto.request.UserSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static javax.management.openmbean.SimpleType.STRING;
import static javax.swing.text.html.parser.DTDConstants.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest extends RestDocsTestSupport {

    @MockBean
    private UserService userService;

    @DisplayName("신규 유저를 생성한다.")
    @Test
    @WithMockUser
    void createUser() throws Exception {
        //given
        UserSaveRequest request = UserSaveRequest.builder()
                .email("kmw106933")
                .password("password123")
                .department("컴퓨터공학부")
                .nickname("미미미누")
                .studentId("202112322")
                .build();

        // when then
        mockMvc.perform(post("/api/v1/users").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("email")
                                        .type(STRING)
                                        .description("멤버 아이디")
                                        .attributes(constraints("아이디 입력은 필수입니다. 최소 6자 이상입니다.")),
                                fieldWithPath("nickname")
                                        .type(STRING)
                                        .description("멤버 닉네임")
                                        .attributes(constraints("닉네임 입력은 필수입니다. 최대 8자 이하입니다.")),
                                fieldWithPath("password")
                                        .type(STRING)
                                        .description("멤버 패스워드")
                                        .attributes(constraints("비밀번호는 영어와 숫자를 포함해서 8자 이상 20자 이내로 입력해주세요.")),
                                fieldWithPath("studentId")
                                        .type(STRING)
                                        .description("멤버 학번")
                                        .attributes(constraints("학번은 20으로 시작하고, 9자리여야 합니다.")),
                                fieldWithPath("department")
                                        .type(STRING)
                                        .description("멤버 학과")
                                        .optional()
                                        .attributes(constraints("ex) 컴퓨터공학부"))
                        ),
                        responseFields(
                                fieldWithPath("code")
                                        .type(NUMBER)
                                        .description("성공시 반환 값"),
                                fieldWithPath("status")
                                        .type(STRING)
                                        .description("상태 값"),
                                 fieldWithPath("message")
                                .type(STRING)
                                .description("상태 값")
                )));
    }


    @DisplayName("이메일 중복을 확인한다.")
    @Test
    void validateEmail() throws Exception {
        //given
        EmailValidationRequest emailValidationRequest = new EmailValidationRequest("kmw106933@naver.com");

        //when then
        mockMvc.perform(post("/api/v1/users/validations/email")
                        .content(objectMapper.writeValueAsString(emailValidationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }


}