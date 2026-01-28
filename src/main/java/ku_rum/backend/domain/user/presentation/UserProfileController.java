package ku_rum.backend.domain.user.presentation;

import static ku_rum.backend.domain.user.domain.UserMessage.SUCCESS_CHANGE_NICKNAME;
import static ku_rum.backend.domain.user.domain.UserMessage.SUCCESS_DEACTIVATE;
import static ku_rum.backend.domain.user.domain.UserMessage.SUCCESS_RESET_PASSWORD;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.SUCCESS_PROFILE_SET;

import jakarta.validation.Valid;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.dto.request.DepartmentRequest;
import ku_rum.backend.domain.user.dto.request.InitiatePasswordResetRequest;
import ku_rum.backend.domain.user.dto.request.NicknameChangeRequest;
import ku_rum.backend.domain.user.dto.request.ProfileChangeRequest;
import ku_rum.backend.domain.user.dto.request.ResetPasswordRequest;
import ku_rum.backend.domain.user.dto.request.S3PresignedUrlRequest;
import ku_rum.backend.domain.user.dto.response.LoginIdResponse;
import ku_rum.backend.domain.user.dto.response.S3PresignedUrlResponse;
import ku_rum.backend.domain.user.dto.response.UserProfileResponse;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserProfileController {

    private final UserService userService;


    /**
     * 프로필 조회 API
     *
     * @return
     */
    @GetMapping("/profile")
    public BaseResponse<UserProfileResponse> getUserProfile() {
        UserProfileResponse response = userService.getUserProfile();
        return BaseResponse.ok(response);
    }

    /**
     * 프로필 변경 API
     *
     * @param profileChangeRequest
     * @return
     **/

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
     **/

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
    public BaseResponse<String> initiatePasswordReset(
            @RequestBody @Valid final InitiatePasswordResetRequest initiatePasswordResetRequest) {
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

    /**
     * 탈퇴 API
     */
    @DeleteMapping("/deactivate")
    public BaseResponse<String> deactivate() {
        userService.deactivate();
        return BaseResponse.ok(SUCCESS_DEACTIVATE.getMessage());
    }

    /**
     * 학과 추가 API
     */
    @PostMapping("/department")
    public BaseResponse<String> addDepartment(@RequestBody @Valid final DepartmentRequest departmentRequest) {
        userService.addDepartment(departmentRequest.department());
        return BaseResponse.ok("학과 추가에 성공하였습니다.");
    }

    /**
     * 학과 삭제 API
     */
    @DeleteMapping("/department")
    public BaseResponse<String> deleteDepartment(@RequestBody @Valid final DepartmentRequest departmentRequest) {
        userService.deleteDepartment(departmentRequest.department());
        return BaseResponse.ok("학과 삭제에 성공하였습니다.");
    }

    /**
     * 프로필 이미지 업로드용 S3 Presigned URL 생성 API
     *
     * @param request
     * @return
     */
    @PostMapping("/profile/presigned-url")
    public BaseResponse<S3PresignedUrlResponse> generatePresignedUrl(
            @RequestBody @Valid final S3PresignedUrlRequest request) {
        S3PresignedUrlResponse response = userService.generateProfileImagePresignedUrl(request);
        return BaseResponse.ok(response);
    }
}

