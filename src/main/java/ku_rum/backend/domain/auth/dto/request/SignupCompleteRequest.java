package ku_rum.backend.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignupCompleteRequest(
        @NotBlank String token,     // 프리사인업 토큰
        @NotBlank String nickname,
        String studentId,
        String imageUrl
) { }
