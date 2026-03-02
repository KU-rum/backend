package ku_rum.backend.domain.user.dto.response;

import jakarta.validation.constraints.NotBlank;

public record TokenResponse(@NotBlank String accessToken, @NotBlank String refreshToken, @NotBlank long accessExpireIn,
                            @NotBlank long refreshExpireIn, boolean isFirstLogin) {

    public static TokenResponse of(String accessToken, String refreshToken, long accessExpireIn, long refreshExpireIn, boolean isFirstLogin) {
        return new TokenResponse(accessToken, refreshToken, accessExpireIn, refreshExpireIn, isFirstLogin);
    }
}
