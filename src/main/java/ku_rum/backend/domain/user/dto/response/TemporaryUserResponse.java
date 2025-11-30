package ku_rum.backend.domain.user.dto.response;

import lombok.Builder;

@Builder
public record TemporaryUserResponse(String accessToken, String refreshToken, long accessExpireIn, long refreshExpireIn,
                                    boolean isFirstLogin) {
    public static TemporaryUserResponse from(TokenResponse token) {
        return TemporaryUserResponse.builder()
                .accessToken(token.accessToken())
                .refreshToken(token.refreshToken())
                .accessExpireIn(token.accessExpireIn())
                .refreshToken(token.refreshToken())
                .isFirstLogin(token.isFirstLogin())
                .build();
    }
}
