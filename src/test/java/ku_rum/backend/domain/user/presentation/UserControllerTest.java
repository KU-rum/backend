package ku_rum.backend.domain.user.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static ku_rum.backend.domain.user.domain.UserMessage.VALID_LOGINID_MESSAGE;
import static ku_rum.backend.domain.user.domain.UserMessage.VALID_NICKNAME_MESSAGE;
import static ku_rum.backend.domain.user.domain.UserMessage.VALID_STUDENTID_MESSAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import ku_rum.backend.config.RestDocsUnitTestSupport;
import ku_rum.backend.domain.alarm.application.FcmService;
import ku_rum.backend.domain.auth.dto.response.AuthResponse;
import ku_rum.backend.domain.common.mail.dto.request.EmailValidationRequest;
import ku_rum.backend.domain.friend.application.FriendReportService;
import ku_rum.backend.domain.friend.domain.repository.FriendBlockRepository;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.application.UserValidator;
import ku_rum.backend.domain.user.domain.AgreementStatus;
import ku_rum.backend.domain.user.dto.request.ProfileChangeRequest;
import ku_rum.backend.domain.user.dto.request.SocialSignupRequest;
import ku_rum.backend.domain.user.dto.request.UserSaveRequest;
import ku_rum.backend.domain.user.dto.response.LoginIdResponse;
import ku_rum.backend.domain.user.dto.response.TokenResponse;
import ku_rum.backend.domain.user.dto.response.UserProfileDepartmentResponse;
import ku_rum.backend.domain.user.dto.response.UserProfileResponse;
import ku_rum.backend.domain.user.dto.response.UserResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.json.JsonType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest({UserController.class, UserProfileController.class})
@ActiveProfiles("test")
class UserControllerTest extends RestDocsUnitTestSupport {

    @MockBean
    private UserService userService;

    @MockBean
    private UserValidator userValidator;

    @MockBean
    private FriendReportService friendManageService;

    @MockBean
    private FriendBlockRepository friendBlockRepository;

    @MockBean
    private FcmService fcmService;

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
                                .tag("멤버 관련 API")
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

    @DisplayName("신규 유저를 소셜 로그인으로 생성한다.")
    @Test
    @WithMockUser
    void createUserBySocial() throws Exception {
        //given
        SocialSignupRequest request = SocialSignupRequest.builder()
                .department("컴퓨터공학부")
                .nickname("미미미누")
                .studentId("202112322")
                .token("asfsdfdsdf12123")
                .agreementStatus(AgreementStatus.AGREED)
                .build();

        // when then
        mockMvc.perform(post("/api/v1/users/social")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization",
                                "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("멤버 관련 API")
                                .description("소셜 로그인 신규 회원 가입 생성")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .requestFields(
                                        fieldWithPath("token")
                                                .type(JsonFieldType.STRING)
                                                .description("소셜 가입 토큰")
                                                .attributes(constraints("소셜 로그인 성공 시 발급되는 프리사인업 토큰")),

                                        fieldWithPath("studentId")
                                                .type(JsonFieldType.STRING)
                                                .description("학번")
                                                .attributes(constraints("20으로 시작하는 9자리 학번 (예: 202112322)")),

                                        fieldWithPath("department")
                                                .type(JsonFieldType.STRING)
                                                .description("학과")
                                                .attributes(constraints("예: 컴퓨터공학부")),

                                        fieldWithPath("nickname")
                                                .type(JsonFieldType.STRING)
                                                .description("닉네임")
                                                .attributes(constraints("2~10자, 중복 불가")),

                                        fieldWithPath("agreementStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("약관 동의 여부")
                                                .attributes(constraints("AGREED 또는 DISAGREED"))
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
                                .tag("멤버 관련 API")
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
        CustomUserDetails userDetails = CustomUserDetails.of(1L, "testUser",
                AuthorityUtils.createAuthorityList("ROLE_USER"), "test12345", false);

        // when then
        mockMvc.perform(patch("/api/v1/users/profile")
                        .header("Authorization",
                                "Bearer access-token")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .content(objectMapper.writeValueAsString(profileChangeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("프로필 관련 API")
                                        .description("프로필 이미지 변경")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
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

    @Test
    @DisplayName("소셜 로그인 회원가입을 완료한다.")
    @WithMockUser
    void completeSocialSignup() throws Exception {
        // given
        SocialSignupRequest request = SocialSignupRequest.builder()
                .token("b9a0e2c6-7b35-4d3b-8b2e-25a7eac2fbc7")
                .studentId("202312345")
                .department("컴퓨터공학과")
                .nickname("민우")
                .agreementStatus(AgreementStatus.AGREED)
                .build();

        AuthResponse mockResponse = AuthResponse.of(
                new TokenResponse("accessToken123", "refreshToken123", 123, 123, true),
                UserResponse.of(
                        1L, "kakao_2392032", null, "user@konkuk.ac.kr",
                        "민우", "202312345", "https://image.kuroom.shop/1.png", List.of()
                )
        );

        given(userService.completeSocialSignup(any(SocialSignupRequest.class)))
                .willReturn(mockResponse);

        // when then
        mockMvc.perform(post("/api/v1/users/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer testAccessToken")
                        .with(SecurityMockMvcRequestPostProcessors.user(
                                CustomUserDetails.of(1L, "testUser",
                                        AuthorityUtils.createAuthorityList("ROLE_USER"), "kakao_2392032", false)
                        ))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("소셜 로그인 API")
                                        .description("소셜 로그인 회원가입 완료 API — 프리사인업 토큰을 이용해 회원 정보를 등록하고 JWT를 발급받습니다.")
                                        .requestHeaders(
                                                headerWithName("Authorization").description(
                                                        "발급받은 임시 접근 토큰 또는 Bearer 헤더 (테스트용)")
                                        )
                                        .requestFields(
                                                fieldWithPath("token")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프리사인업 토큰 (PreSignupTokenProvider에서 발급된 1회용 토큰)")
                                                        .attributes(constraints("소셜 로그인 성공 시 프론트로 전달된 토큰을 그대로 전송합니다.")),
                                                fieldWithPath("studentId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("학번 (예: 202312345)")
                                                        .attributes(constraints("9자리 학번, 20으로 시작해야 합니다.")),
                                                fieldWithPath("department")
                                                        .type(JsonFieldType.STRING)
                                                        .description("학과 이름")
                                                        .attributes(constraints("예: 컴퓨터공학과")),
                                                fieldWithPath("nickname")
                                                        .type(JsonFieldType.STRING)
                                                        .description("닉네임 (2~10자)")
                                                        .attributes(constraints("특수문자 제외, 중복 불가")),
                                                fieldWithPath("agreementStatus")
                                                        .type(JsonFieldType.STRING)
                                                        .description("약관 동의 상태 (예: AGREED)")
                                                        .attributes(constraints("반드시 AGREED 여야 합니다."))
                                        )
                                        .responseFields(
                                                // tokenResponse
                                                fieldWithPath("data.tokenResponse.accessToken")
                                                        .type(JsonFieldType.STRING)
                                                        .description("Access Token"),
                                                fieldWithPath("data.tokenResponse.refreshToken")
                                                        .type(JsonFieldType.STRING)
                                                        .description("Refresh Token"),
                                                fieldWithPath("data.tokenResponse.accessExpireIn")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("Access Token 만료까지 남은 시간(초)"),
                                                fieldWithPath("data.tokenResponse.refreshExpireIn")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("Refresh Token 만료까지 남은 시간(초)"),
                                                fieldWithPath("data.tokenResponse.isFirstLogin")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("첫 로그인 여부"),

                                                // userResponse (여기가 핵심!)
                                                subsectionWithPath("data.userResponse")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("회원 정보 객체"),

                                                fieldWithPath("data.userResponse.id")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("회원 고유 ID"),
                                                fieldWithPath("data.userResponse.oauthId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("OAuth ID").optional(),
                                                fieldWithPath("data.userResponse.loginId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그인 ID").optional(),
                                                fieldWithPath("data.userResponse.email")
                                                        .type(JsonFieldType.STRING)
                                                        .description("이메일").optional(),
                                                fieldWithPath("data.userResponse.nickname")
                                                        .type(JsonFieldType.STRING)
                                                        .description("닉네임"),
                                                fieldWithPath("data.userResponse.studentId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("학번"),
                                                fieldWithPath("data.userResponse.imageUrl")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 URL").optional(),
                                                fieldWithPath("data.userResponse.departmentResponse")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("학과 리스트").optional(),
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("응답 코드 (200)"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("응답 상태 (OK)"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("응답 메시지")
                                        )
                                        .build()
                        )
                ));
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
                                .tag("멤버 관련 API")
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
                                .tag("멤버 관련 API")
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
                                .tag("멤버 관련 API")
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
                                .tag("멤버 관련 API")
                                .description("이메일을 이용하여 로그인 아이디 조회")
                                .queryParameters(
                                        parameterWithName("email").description("조회할 유저의 이메일")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.loginId").type(JsonFieldType.STRING)
                                                .description("조회된 로그인 아이디")
                                ).build())));
    }

    @Test
    @DisplayName("회원 프로필 조회")
    void getUserProfile() throws Exception {

        //given
        List<UserProfileDepartmentResponse> profileDepartmentResponses = List.of(
                new UserProfileDepartmentResponse("융합생명공학", "KU융합과학기술원"));
        UserProfileResponse response = new UserProfileResponse("examplce.png", "kuroom@konkuk.ac.kr", "kuroom", "쿠룸",
                "201911254",
                profileDepartmentResponses);
        when(userService.getUserProfile()).thenReturn(response);

        // when & Then
        mockMvc.perform(get("/api/v1/users/profile")
                        .header("Authorization",
                                "Bearer access-token")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("프로필 관련 API")
                                        .description("프로필 조회")
                                        .requestHeaders(
                                                headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
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
                                                fieldWithPath("data.profileImage")
                                                        .type(JsonType.STRING)
                                                        .description("회원 프로필 이미지 URL"),
                                                fieldWithPath("data.email")
                                                        .type(JsonType.STRING)
                                                        .description("회원 이메일"),
                                                fieldWithPath("data.loginId")
                                                        .type(JsonType.STRING)
                                                        .description("회원 로그인 아이디"),
                                                fieldWithPath("data.nickname")
                                                        .type(JsonType.STRING)
                                                        .description("회원 닉네임"),
                                                fieldWithPath("data.studentId")
                                                        .type(JsonType.STRING)
                                                        .description("회원 학번"),
                                                fieldWithPath("data.departments[].department")
                                                        .type(JsonType.STRING)
                                                        .description("회원 학과"),
                                                fieldWithPath("data.departments[].college")
                                                        .type(JsonType.STRING)
                                                        .description("회원 단과대")
                                        ).build())));
    }
}