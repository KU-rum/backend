package ku_rum.backend.domain.user.presentation;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import ku_rum.backend.config.RestDocsTestSupport;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepositoryImpl;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.common.mail.dto.request.EmailValidationRequest;
import ku_rum.backend.domain.user.application.UserValidator;
import ku_rum.backend.domain.user.domain.AgreementStatus;
import ku_rum.backend.domain.user.dto.request.ProfileChangeRequest;
import ku_rum.backend.domain.user.dto.request.UserSaveRequest;
import ku_rum.backend.domain.user.dto.response.LoginIdResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.batch.BatchScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.json.JsonType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static ku_rum.backend.domain.user.domain.UserMessage.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserControllerTest extends RestDocsTestSupport {

    @MockBean
    private UserService userService;

    @MockBean
    private BatchScheduler batchScheduler;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserValidator userValidator;

    @MockBean
    private NoticeRepositoryImpl noticeRepository;

    @DisplayName("신규 유저를 생성한다.")
    @Test
    @WithMockUser
    void createUser() throws Exception {
        //given
        UserSaveRequest request = UserSaveRequest.builder()
                .email("kmw106933@konkuk.ac.kr")
                .loginId("kmw106933")
                .password("password123")
                .department("컴퓨터공학부")
                .nickname("미미미누")
                .studentId("202112322")
                .agreementStatus(AgreementStatus.AGREED)
                .build();

        // when then
        mockMvc.perform(post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("유저 API")
                                .description("신규 유저 생성")
                                .requestFields(
                                        fieldWithPath("loginId")
                                                .type(JsonType.STRING)
                                                .description("멤버 아이디")
                                                .attributes(constraints("아이디 입력은 필수입니다. 최소 6자 이상입니다.")),
                                        fieldWithPath("email")
                                                .type(JsonType.STRING)
                                                .description("멤버 이메일")
                                                .attributes(constraints("유저의 이메일")),
                                        fieldWithPath("nickname")
                                                .type(JsonType.STRING)
                                                .description("멤버 닉네임")
                                                .attributes(constraints("닉네임 입력은 필수입니다. 최대 8자 이하입니다.")),
                                        fieldWithPath("password")
                                                .type(JsonType.STRING)
                                                .description("멤버 패스워드")
                                                .attributes(constraints("비밀번호는 영어와 숫자를 포함해서 8자 이상 20자 이내로 입력해주세요.")),
                                        fieldWithPath("studentId")
                                                .type(JsonType.STRING)
                                                .description("멤버 학번")
                                                .attributes(constraints("학번은 20으로 시작하고, 9자리여야 합니다.")),
                                        fieldWithPath("department")
                                                .type(JsonType.STRING)
                                                .description("멤버 학과")
                                                .attributes(constraints("ex) 컴퓨터공학부")),
                                        fieldWithPath("agreementStatus")
                                                .type(JsonType.STRING)
                                                .description("선택 동의 여부")
                                                .attributes(constraints("ex) AGREED/DISAGREED"))
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
                                                .description("성공 시 메시지 (OK)")
                                ).build())));
    }


    @DisplayName("이메일 중복을 확인한다.")
    @Test
    void validateEmail() throws Exception {
        //given
        EmailValidationRequest loginIdValidationRequest = new EmailValidationRequest("kmw106933@naver.com");
        //when then
        mockMvc.perform(post("/api/v1/users/validations")
                        .content(objectMapper.writeValueAsString(loginIdValidationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value("올바른 이메일 입니다."))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("유저 API")
                                .description("이메일 중복 확인")
                                .requestFields(
                                        fieldWithPath("email")
                                                .type(JsonType.STRING)
                                                .description("멤버 이메일")
                                                .attributes(constraints("중복 확인할 이메일"))
                                )
                                .responseFields(
                                        fieldWithPath("code")
                                                .type(JsonType.NUMBER)
                                                .description("성공시 반환 코드 (200)"),
                                        fieldWithPath("status")
                                                .type(JsonType.STRING)
                                                .description("성공시 상태 값 (OK)"),
                                        fieldWithPath("message")
                                                .type(JsonType.STRING)
                                                .description("성공 시 메시지 값 (OK)"),
                                        fieldWithPath("data")
                                                .type(JsonType.STRING)
                                                .description("성공 시 '올바른 이메일 입니다.' 반환")
                                ).build())));
    }

    @Test
    @DisplayName("프로필 이미지를 변경한다.")
    @WithMockUser
    void changeProfile() throws Exception {
        // given
        ProfileChangeRequest profileChangeRequest = new ProfileChangeRequest("test.com");
        CustomUserDetails userDetails = CustomUserDetails.of(1L, "testUser", AuthorityUtils.createAuthorityList("ROLE_USER"), "test12345");

        // when then
        mockMvc.perform(patch("/api/v1/users/profile")
                        .header("Bearer", "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyUEsiOjEsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzQwMjQyNjQxLCJleHAiOjE3NDAyNDQ0NDF9.kLSMBLWdvIvrBpGJdOigSKjxMIab0cV06xFjSpwrq70")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .content(objectMapper.writeValueAsString(profileChangeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("유저 API")
                                        .description("프로필 이미지 변경")
                                        .requestHeaders(
                                                headerWithName("Bearer").description("발급 받은 엑세스 토큰입니다.")
                                        )
                                        .requestFields(
                                                fieldWithPath("imageUrl")
                                                        .type(JsonType.STRING)
                                                        .description("새로 변경할 프로필")
                                                        .attributes(constraints("새로 변경할 프로필입니다."))
                                        )
                                        .responseFields(
                                                fieldWithPath("code")
                                                        .type(JsonType.NUMBER)
                                                        .description("성공시 반환 코드 (200)"),
                                                fieldWithPath("status")
                                                        .type(JsonType.STRING)
                                                        .description("성공시 상태 값 (OK)"),
                                                fieldWithPath("message")
                                                        .type(JsonType.STRING)
                                                        .description("성공 시 메시지 값 (OK)"),
                                                fieldWithPath("data")
                                                        .type(JsonType.STRING)
                                                        .description("성공 시 반환 메시지")
                                        ).build())));
    }

    @DisplayName("아이디 중복 여부를 확인한다.")
    @Test
    void checkDuplicateId() throws Exception {
        // given
        String testValue = "testUser";
        doNothing().when(userValidator).validateDuplicateLoginId(testValue);

        // when then
        mockMvc.perform(get("/api/v1/users/check-id")
                        .param("value", testValue)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(VALID_LOGINID_MESSAGE.getMessage()))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("유저 API")
                                .description("아이디 중복 확인")
                                .queryParameters(
                                        parameterWithName("value").description("중복 확인할 아이디")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("중복 여부 메시지")
                                ).build())));
    }

    @Test
    @DisplayName("닉네임 중복 여부를 확인한다.")
    void checkDuplicateNickname() throws Exception {
        // given
        String testNickname = "testNickname";
        doNothing().when(userValidator).validateNickname(testNickname);

        // when & then
        mockMvc.perform(get("/api/v1/users/check-nickname")
                        .param("value", testNickname)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(VALID_NICKNAME_MESSAGE.getMessage()))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("유저 API")
                                .description("닉네임 중복 확인")
                                .queryParameters(
                                        parameterWithName("value").description("중복 확인할 닉네임")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("유효한 닉네임 메시지")
                                ).build())));
    }

    @Test
    @DisplayName("학번 중복 여부를 확인한다.")
    void checkDuplicateStudentId() throws Exception {
        // given
        String testStudentId = "2021123456";
        doNothing().when(userValidator).validateDuplicateStudentId(testStudentId);


        // when & then
        mockMvc.perform(get("/api/v1/users/check-studentId")
                        .param("value", testStudentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(VALID_STUDENTID_MESSAGE.getMessage()))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("유저 API")
                                .description("학번 중복 확인")
                                .queryParameters(
                                        parameterWithName("value").description("중복 확인할 학번")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("유효한 학번 메시지")
                                ).build())));
    }

    @Test
    @DisplayName("이메일로 로그인 아이디 조회 성공")
    void getLoginId_Success() throws Exception {
        // Given
        String email = "test@example.com";
        String loginId = "testUser";
        LoginIdResponse response = new LoginIdResponse(loginId);

        when(userService.getLoginId(email)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/users/loginId")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.loginId").value(loginId))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("유저 API")
                                .description("이메일을 이용하여 로그인 아이디 조회")
                                .queryParameters(
                                        parameterWithName("email").description("조회할 유저의 이메일")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.loginId").type(JsonFieldType.STRING).description("조회된 로그인 아이디")
                                ).build())));
    }
}