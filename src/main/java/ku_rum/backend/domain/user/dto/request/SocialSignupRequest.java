package ku_rum.backend.domain.user.dto.request;

import jakarta.validation.constraints.*;
import ku_rum.backend.domain.user.domain.AgreementStatus;
import lombok.Builder;

@Builder
public record SocialSignupRequest(
        @NotBlank(message = "소셜 가입 토큰은 필수입니다.")
        String token,

        @NotNull(message = "학번 입력은 필수입니다.")
        @Pattern(regexp = "^20(1[0-9]|2[0-5])\\d{5}$", message = "학번은 20으로 시작하고, 9자리여야 합니다.")
        String studentId,

        @NotNull(message = "학과 입력은 필수입니다.")
        String department,

        @NotBlank(message = "닉네임 입력은 필수입니다. 최대 8자 이하입니다.")
        @Size(min = 2, max = 10)
        String nickname,

        @NotNull(message = "약관 동의 여부는 필수 입니다.")
        AgreementStatus agreementStatus
) {
}
