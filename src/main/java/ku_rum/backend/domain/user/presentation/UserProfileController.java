package ku_rum.backend.domain.user.presentation;

import jakarta.validation.Valid;
import ku_rum.backend.domain.common.image.application.ImageStorageService;
import ku_rum.backend.domain.common.image.dto.response.ImageResponse;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.dto.request.NicknameChangeRequest;
import ku_rum.backend.domain.user.dto.request.ProfileChangeRequest;
import ku_rum.backend.domain.user.dto.request.InitiatePasswordResetRequest;
import ku_rum.backend.domain.user.dto.request.ResetPasswordRequest;
import ku_rum.backend.domain.user.dto.response.LoginIdResponse;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static ku_rum.backend.domain.user.domain.UserMessage.*;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.SUCCESS_PROFILE_SET;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserProfileController {

    private final UserService userService;
    private final ImageStorageService imageStorageService;

    /**
     * 프로필 변경 API
     *
     * @param profileChangeRequest
     * @return
     */
    @PatchMapping("/profile")
    public BaseResponse<String> setProfile(@RequestBody @Valid final ProfileChangeRequest profileChangeRequest) {
        userService.changeProfile(profileChangeRequest);
        return BaseResponse.ok(SUCCESS_PROFILE_SET.getMessage());
    }

    /**
     * 이메일로 아이디 가져오기 API
     *
     * @param email
     * @return
     */
    @GetMapping("/loginId")
    public BaseResponse<LoginIdResponse> getLoginId(@RequestParam("email") final String email) {
        return BaseResponse.ok(userService.getLoginId(email));
    }

    /**
     * 닉네임 변경 API
     *
     * @return
     */
    @PatchMapping("/nickname")
    public BaseResponse<String> setProfile(@RequestBody @Valid final NicknameChangeRequest nicknameChangeRequest) {
        userService.changeNickname(nicknameChangeRequest);
        return BaseResponse.ok(SUCCESS_CHANGE_NICKNAME.getMessage());
    }

    /**
     * 로그인 전 아이디로 비밀번호 초기화 API
     *
     * @param initiatePasswordResetRequest
     * @return
     */
    @PostMapping("/password-reset/initiate")
    public BaseResponse<String> initiatePasswordReset(@RequestBody @Valid final InitiatePasswordResetRequest initiatePasswordResetRequest) {
        userService.initiatePasswordReset(initiatePasswordResetRequest);
        return BaseResponse.ok(SUCCESS_RESET_PASSWORD.getMessage());
    }

    /**
     * 로그인 후 아이디로 비밀번호 초기화 API
     *
     * @return
     */
    @PostMapping("/password-reset")
    public BaseResponse<String> resetPassword(@RequestBody @Valid final ResetPasswordRequest resetPasswordRequest) {
        userService.resetPassword(resetPasswordRequest);
        return BaseResponse.ok(SUCCESS_RESET_PASSWORD.getMessage());
    }
}
