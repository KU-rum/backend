package ku_rum.backend.domain.user.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "사용자 이메일은 필수입니다.") String email,
        @NotBlank(message = "사용자 비밀번호는 필수입니다.") String password
) {
}
