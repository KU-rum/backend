package ku_rum.backend.domain.user.presentation;

import static ku_rum.backend.domain.user.domain.UserMessage.VALID_EMAIL_MESSAGE;
import static ku_rum.backend.domain.user.domain.UserMessage.VALID_LOGINID_MESSAGE;
import static ku_rum.backend.domain.user.domain.UserMessage.VALID_NICKNAME_MESSAGE;
import static ku_rum.backend.domain.user.domain.UserMessage.VALID_STUDENTID_MESSAGE;

import jakarta.validation.Valid;
import ku_rum.backend.domain.alarm.application.FcmService;
import ku_rum.backend.domain.auth.dto.response.AuthResponse;
import ku_rum.backend.domain.common.mail.dto.request.EmailValidationRequest;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.application.UserValidator;
import ku_rum.backend.domain.user.dto.request.SocialSignupRequest;
import ku_rum.backend.domain.user.dto.request.UserFcmRequest;
import ku_rum.backend.domain.user.dto.request.UserSaveRequest;
import ku_rum.backend.domain.user.dto.response.UserFcmResponse;
import ku_rum.backend.domain.user.dto.response.UserSaveResponse;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;
    private final FcmService fcmService;

    /**
     * 회원 가입 API
     *
     * @param userSaveRequest
     * @return
     */
    @PostMapping
    public BaseResponse<UserSaveResponse> join(@RequestBody @Valid final UserSaveRequest userSaveRequest) {
        return BaseResponse.ok(userService.saveUser(userSaveRequest));
    }

    /**
     * 소셜 로그인 회원 가입 API (토큰 필요)
     *
     * @return
     */
    @PostMapping("/social")
    public BaseResponse<AuthResponse> joinBySocial(@RequestBody @Valid final SocialSignupRequest req) {
        return BaseResponse.ok(userService.completeSocialSignup(req));
    }

    /**
     * 이메일 검증 API
     *
     * @param emailValidationRequest
     * @return
     */
    @PostMapping("/validations")
    public BaseResponse<String> validateEmail(@RequestBody @Valid final EmailValidationRequest emailValidationRequest) {
        userValidator.validateDuplicateEmail(emailValidationRequest.email());
        return BaseResponse.ok(VALID_EMAIL_MESSAGE.getMessage());
    }

    /**
     * 아이디 중복 확인 API
     *
     * @param value
     * @return
     */
    @GetMapping("/check-id")
    public BaseResponse<String> checkDuplicateId(@RequestParam("value") final String value) {
        userValidator.validateDuplicateLoginId(value);
        return BaseResponse.ok(VALID_LOGINID_MESSAGE.getMessage());
    }

    /**
     * 닉네임 중복 확인 API
     *
     * @param value
     * @return
     */
    @GetMapping("/check-nickname")
    public BaseResponse<String> checkDuplicateNickname(@RequestParam("value") final String value) {
        userValidator.validateNickname(value);
        return BaseResponse.ok(VALID_NICKNAME_MESSAGE.getMessage());
    }

    /**
     * 학번 중복 확인 API
     *
     * @param value
     * @return
     */
    @GetMapping("/check-studentId")
    public BaseResponse<String> checkDuplicateStudentId(@RequestParam("value") final String value) {
        userValidator.validateDuplicateStudentId(value);
        return BaseResponse.ok(VALID_STUDENTID_MESSAGE.getMessage());
    }

    @PostMapping("/fcm")
    public BaseResponse<UserFcmResponse> createFcmToken(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                                        @RequestBody final UserFcmRequest request) {
        UserFcmResponse response = fcmService.createFcmToken(userDetails, request);
        return BaseResponse.ok(response);
    }
}


