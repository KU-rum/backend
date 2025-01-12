package ku_rum.backend.global.security.jwt;

import jakarta.validation.constraints.NotBlank;

public record TokenResponse(@NotBlank String accessToken, @NotBlank String refreshToken, @NotBlank long accessExpireIn,
                            @NotBlank long refreshExpireIn) {

    public static TokenResponse of(String accessToken, String refreshToken, long accessExpireIn, long refreshExpireIn) {
        return new TokenResponse(accessToken, refreshToken, accessExpireIn, refreshExpireIn);
    }
}
