package ku_rum.backend.domain.user.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(
        @NotBlank String refreshToken
) {
}
