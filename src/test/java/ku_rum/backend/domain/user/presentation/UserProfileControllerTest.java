package ku_rum.backend.domain.user.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import ku_rum.backend.config.RestDocsUnitTestSupport;
import ku_rum.backend.domain.auth.application.TokenBlacklistService;
import ku_rum.backend.domain.common.mail.dto.request.MailVerificationRequest;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.dto.request.DepartmentRequest;
import ku_rum.backend.domain.user.dto.request.InitiatePasswordResetRequest;
import ku_rum.backend.domain.user.dto.request.NicknameChangeRequest;
import ku_rum.backend.domain.user.dto.request.ResetPasswordRequest;
import ku_rum.backend.domain.user.dto.request.S3PresignedUrlRequest;
import ku_rum.backend.domain.user.dto.response.S3PresignedUrlResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(UserProfileController.class)
@ActiveProfiles("test")
public class UserProfileControllerTest extends RestDocsUnitTestSupport {

    @MockBean
    private UserService userService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    @DisplayName("닉네임 변경 성공")
    @WithMockUser
    void changeNickname_Success() throws Exception {
        // Given
        NicknameChangeRequest request = new NicknameChangeRequest("안녕안녕");
        CustomUserDetails userDetails = CustomUserDetails.of(1L, "testUser",
                AuthorityUtils.createAuthorityList("ROLE_USER"), "test12345", false);
        String requestBody = new ObjectMapper().writeValueAsString(request);

        // When & Then
        mockMvc.perform(patch("/api/v1/users/nickname")
                        .header("Authorization",
                                "Bearer access-token")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value("닉네임이 변경되었습니다."))
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("프로필 관련 API")
                                .description("닉네임 변경 API")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 엑세스 토큰입니다.")
                                )
                                .requestFields(
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("변경할 닉네임")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("응답 완료 메시지")
                                )
                                .build())));

        // Verify
        verify(userService, times(1)).changeNickname(any(NicknameChangeRequest.class));
    }

    @Test
    @DisplayName("비밀번호를 변경한다.")
    @WithMockUser
    void resetAccount() throws Exception {
        // given
        MailVerificationRequest request = new MailVerificationRequest("kmw106933@naver.com", "1234");
        InitiatePasswordResetRequest initiatePasswordResetRequest = new InitiatePasswordResetRequest(request, "user123",
                "test12345");

        // when then
        mockMvc.perform(post("/api/v1/users/password-reset/initiate")
                        .content(objectMapper.writeValueAsString(initiatePasswordResetRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("프로필 관련 API")
                                .description("로그인 전 비밀번호 변경")
                                .requestFields(
                                        fieldWithPath("emailRequest.email")
                                                .type(JsonFieldType.STRING)
                                                .description("인증 이메일 주소"),
                                        fieldWithPath("emailRequest.code")
                                                .type(JsonFieldType.STRING)
                                                .description("이메일 인증 코드"),
                                        fieldWithPath("loginId")
                                                .type(JsonFieldType.STRING)
                                                .description("비밀번호 변경할 아이디")
                                                .attributes(constraints("비밀번호를 변경할 아이디입니다.")),
                                        fieldWithPath("newPassword")
                                                .type(JsonFieldType.STRING)
                                                .description("새 비밀번호")
                                                .attributes(constraints("새 비밀번호입니다."))
                                )
                                .responseFields(
                                        fieldWithPath("code")
                                                .type(JsonFieldType.NUMBER)
                                                .description("성공시 반환 코드 (200)"),
                                        fieldWithPath("status")
                                                .type(JsonFieldType.STRING)
                                                .description("성공시 상태 값 (OK)"),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("성공 시 메시지 값 (OK)"),
                                        fieldWithPath("data")
                                                .type(JsonFieldType.STRING)
                                                .description("성공 시 '아이디/비밀번호가 변경되었습니다.' 반환")
                                ).build())));
    }

    @Test
    @DisplayName("기존 아이디로 비밀번호를 변경한다.")
    @WithMockUser
    void passwordReset() throws Exception {
        // given
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest("test1234", "test12345");
        CustomUserDetails userDetails = CustomUserDetails.of(1L, "testUser",
                AuthorityUtils.createAuthorityList("ROLE_USER"), "test12345", false);

        // when then
        mockMvc.perform(post("/api/v1/users/password-reset")
                        .header("Authorization",
                                "Bearer access-token")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .content(objectMapper.writeValueAsString(resetPasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(resource(
                        ResourceSnippetParameters.builder()
                                .tag("프로필 관련 API")
                                .description("로그인 후 비밀번호 변경")
                                .requestHeaders(
                                        headerWithName("Authorization").description(
                                                "발급 받은 엑세스 토큰입니다. Authorization 헤더에 토큰을 넣어주세요. 앞에 Bearer를 붙혀야 합니다.")
                                )
                                .requestFields(
                                        fieldWithPath("prevPassword")
                                                .type(JsonFieldType.STRING)
                                                .description("기존 비밀번호")
                                                .attributes(constraints("기존 비밀번호입니다.")),
                                        fieldWithPath("newPassword")
                                                .type(JsonFieldType.STRING)
                                                .description("새 비밀번호")
                                                .attributes(constraints("새 비밀번호입니다."))
                                )
                                .responseFields(
                                        fieldWithPath("code")
                                                .type(JsonFieldType.NUMBER)
                                                .description("성공시 반환 코드 (200)"),
                                        fieldWithPath("status")
                                                .type(JsonFieldType.STRING)
                                                .description("성공시 상태 값 (OK)"),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("성공 시 메시지 값 (OK)"),
                                        fieldWithPath("data")
                                                .type(JsonFieldType.STRING)
                                                .description("성공 시 '아이디/비밀번호가 변경되었습니다.' 반환")
                                ).build())));
    }

    @Test
    @DisplayName("사용자가 계정을 탈퇴한다.")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void deactivateUser() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/users/deactivate")
                        .header("Authorization",
                                "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200)) // 성공 코드 확인
                .andExpect(jsonPath("$.status").value("OK")) // 상태 값 확인
                .andExpect(jsonPath("$.message").value("OK")) // 반환 메시지 확인
                .andExpect(jsonPath("$.data").value("탈퇴가 완료되었습니다.")) // 반환 데이터 없음 확인
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("권한 관련 API")
                                .description("회원 탈퇴 API")
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("발급 받은 액세스 토큰 (Bearer {token})")
                                )
                                .responseFields(
                                        fieldWithPath("code")
                                                .type(JsonFieldType.NUMBER)
                                                .description("성공 시 반환 코드 (200)"),
                                        fieldWithPath("status")
                                                .type(JsonFieldType.STRING)
                                                .description("성공 시 상태 값 (OK)"),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("성공 시 메시지 값 (회원 탈퇴가 완료되었습니다.)"),
                                        fieldWithPath("data")
                                                .type(JsonFieldType.STRING)
                                                .description("성공 시 메시지 값 (회원 탈퇴가 완료되었습니다.)")
                                ).build()
                        )
                ));

        // userService.deactivate()가 호출되었는지 검증
        verify(userService, times(1)).deactivate();
    }


    @Test
    @DisplayName("학과 추가 API")
    @WithMockUser(roles = "USER")
    void addDepartment() throws Exception {
        // given
        DepartmentRequest req = new DepartmentRequest("컴퓨터공학과");

        // when & then
        mockMvc.perform(post("/api/v1/users/department")
                        .header("Authorization",
                                "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("학과 추가에 성공하였습니다."))
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("프로필 관련 API")
                                .description("학과 추가 API")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰 (Bearer {token})")
                                )
                                .requestFields(
                                        fieldWithPath("department")
                                                .type(JsonFieldType.STRING)
                                                .description("추가할 학과 이름")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("학과 추가 성공 메시지")
                                )
                                .build()
                        )));

        verify(userService, times(1)).addDepartment("컴퓨터공학과");
    }

    @Test
    @DisplayName("학과 삭제 API")
    @WithMockUser(roles = "USER")
    void deleteDepartment() throws Exception {
        // given
        DepartmentRequest req = new DepartmentRequest("컴퓨터공학과");

        // when & then
        mockMvc.perform(delete("/api/v1/users/department")
                        .header("Authorization",
                                "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("학과 삭제에 성공하였습니다."))
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("프로필 관련 API")
                                .description("학과 삭제 API")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰 (Bearer {token})")
                                )
                                .requestFields(
                                        fieldWithPath("department")
                                                .type(JsonFieldType.STRING)
                                                .description("삭제할 학과 이름")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드 (200)"),
                                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태 (OK)"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("학과 삭제 성공 메시지")
                                ).build()
                        )));

        verify(userService, times(1)).deleteDepartment("컴퓨터공학과");
    }

    @Test
    @DisplayName("프로필 이미지 업로드용 S3 Presigned URL 생성 API")
    @WithMockUser(roles = "USER")
    void generatePresignedUrl() throws Exception {
        // given
        S3PresignedUrlRequest request = new S3PresignedUrlRequest(
                "profile.jpg",
                "image/jpeg"
        );

        S3PresignedUrlResponse mockResponse = new S3PresignedUrlResponse(
                "https://bucket.s3.ap-northeast-2.amazonaws.com/profile/uuid_profile.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&...",
                "profile/uuid_profile.jpg",
                "https://bucket.s3.ap-northeast-2.amazonaws.com/profile/uuid_profile.jpg"
        );

        when(userService.generateProfileImagePresignedUrl(any(S3PresignedUrlRequest.class)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/v1/users/profile/presigned-url")
                        .header("Authorization",
                                "Bearer access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.presignedUrl").exists())
                .andExpect(jsonPath("$.data.fileKey").exists())
                .andExpect(jsonPath("$.data.fullUrl").exists())
                .andDo(restDocs.document(
                        resource(ResourceSnippetParameters.builder()
                                .tag("프로필 관련 API")
                                .description("프로필 이미지 업로드용 S3 Presigned URL 생성 API\n\n" +
                                        "### 사용 방법\n" +
                                        "1. 이 API를 호출하여 Presigned URL을 받습니다.\n" +
                                        "2. 클라이언트에서 받은 presignedUrl로 이미지를 직접 S3에 PUT 요청으로 업로드합니다.\n" +
                                        "3. 업로드 성공 후 fullUrl을 프로필 변경 API에 전달합니다.\n\n" +
                                        "### 주의사항\n" +
                                        "- Presigned URL은 15분간 유효합니다.\n" +
                                        "- 허용되는 파일 타입: image/jpeg, image/jpg, image/png, image/webp\n" +
                                        "- 최대 파일 크기: 5MB")
                                .requestHeaders(
                                        headerWithName("Authorization").description("발급 받은 액세스 토큰 (Bearer {token})")
                                )
                                .requestFields(
                                        fieldWithPath("fileName")
                                                .type(JsonFieldType.STRING)
                                                .description("업로드할 파일명 (예: profile.jpg)"),
                                        fieldWithPath("fileType")
                                                .type(JsonFieldType.STRING)
                                                .description("파일 MIME 타입 (예: image/jpeg, image/png)")
                                )
                                .responseFields(
                                        fieldWithPath("code")
                                                .type(JsonFieldType.NUMBER)
                                                .description("응답 코드 (200)"),
                                        fieldWithPath("status")
                                                .type(JsonFieldType.STRING)
                                                .description("응답 상태 (OK)"),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data.presignedUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("S3에 업로드할 때 사용하는 Presigned URL (15분간 유효)"),
                                        fieldWithPath("data.fileKey")
                                                .type(JsonFieldType.STRING)
                                                .description("S3에 저장될 파일의 키 (예: profile/uuid_filename.jpg)"),
                                        fieldWithPath("data.fullUrl")
                                                .type(JsonFieldType.STRING)
                                                .description("업로드 완료 후 접근 가능한 최종 이미지 URL (프로필 변경 API에 전달)")
                                )
                                .build()
                        )));

        verify(userService, times(1)).generateProfileImagePresignedUrl(any(S3PresignedUrlRequest.class));
    }
}