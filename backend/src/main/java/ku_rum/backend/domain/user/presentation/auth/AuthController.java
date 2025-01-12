package ku_rum.backend.domain.user.presentation.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ku_rum.backend.domain.user.application.auth.AuthService;
import ku_rum.backend.domain.user.dto.request.auth.LoginRequest;
import ku_rum.backend.domain.user.dto.request.auth.ReissueRequest;
import ku_rum.backend.global.response.BaseResponse;
import ku_rum.backend.global.security.jwt.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 유저 로그인 API
     * @param authRequest email, password
     * @return TokenResponse AccessToken, RefreshToken, accessTokenTime, refreshTokenTime
     */
    @PostMapping("/login")
    public BaseResponse<TokenResponse> login(@Valid @RequestBody LoginRequest authRequest) {
        return BaseResponse.ok(authService.login(authRequest));
    }

    /**
     * 유저 로그아웃 API
     * @return 정상 로그아웃 시 "로그아웃이 완료되었습니다"
     */
    @PatchMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return BaseResponse.ok("로그아웃이 완료되었습니다.");
    }

    /**
     * 토큰 재발급 API
     * @param reissueRequest refreshToken
     * @return TokenResponse AccessToken, RefreshToken, accessTokenTime, refreshTokenTime
     */
    @PatchMapping("/reissue")
    public BaseResponse<TokenResponse> reissue(@Valid @RequestBody ReissueRequest reissueRequest) {
        return BaseResponse.ok(authService.reissue(reissueRequest));
    }
}
