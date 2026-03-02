package ku_rum.backend.domain.auth.application;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import ku_rum.backend.domain.auth.dto.request.LoginRequest;
import ku_rum.backend.domain.auth.dto.request.ReissueRequest;
import ku_rum.backend.domain.auth.dto.response.AuthResponse;
import ku_rum.backend.domain.common.firebase.application.NotificationService;
import ku_rum.backend.domain.department.domain.UserDepartment;
import ku_rum.backend.domain.department.domain.repository.UserDepartmentRepository;
import ku_rum.backend.domain.department.dto.DepartmentResponse;
import ku_rum.backend.domain.oauth.handler.TempTokenProvider;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.domain.user.dto.response.TokenResponse;
import ku_rum.backend.domain.user.dto.response.UserResponse;
import ku_rum.backend.global.exception.user.NoSuchUserException;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import ku_rum.backend.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.DELETED_MEMBER;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.MALFORMED_TOKEN;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_USER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;
    private final TokenBlacklistService tokenBlacklistService;
    private final NotificationService notificationService;
    private final TempTokenProvider tempTokenProvider;
    private final UserRepository userRepository;
    private final UserDepartmentRepository userDepartmentRepository;

    public AuthResponse login(LoginRequest authRequest) {
        try {
            Authentication authenticate = authenticateUser(authRequest);
            CustomUserDetails customUserDetails = getCustomUserDetails(authenticate);

            User user = getUser(customUserDetails);
            validateUser(user);
            return AuthResponse.of(jwtTokenProvider.createToken(authenticate), buildUserResponse(user));

        } catch (AuthenticationException e) {
            log.error("로그인 오류 발생: {}", authRequest.loginId(), e);
            throw new BadCredentialsException("[유저에 대한 로그인 오류 발생]");
        }
    }

    public void logout(HttpServletRequest request) {
        String token = validateAccessToken(request);
        Long userId = jwtTokenProvider.getUserId(token);

        long expiredAccessTokenTime = getExpiredAccessTokenTime(token);
        notificationService.deleteToken(userId);
        log.debug("FCM 토큰 만료 완료");
        tokenBlacklistService.setBlackListInRedis(token, expiredAccessTokenTime, userId);
        log.info("사용자 {} 가 로그인 했습니다.", userId);
    }

    public TokenResponse reissue(ReissueRequest reissueRequest) {
        jwtTokenProvider.validateToken(reissueRequest.refreshToken());

        Authentication authenticate = jwtTokenProvider.getAuthentication(reissueRequest.refreshToken());
        CustomUserDetails principal = (CustomUserDetails) authenticate.getPrincipal();

        if (tokenBlacklistService.validateRefreshTokenInRedis(reissueRequest, principal))
            return jwtTokenProvider.createToken(authenticate);

        log.error("토큰 재발급 오류 발생: {}", MALFORMED_TOKEN.getMessage());
        throw new JwtException(MALFORMED_TOKEN.getMessage());
    }

    public AuthResponse exchangeToken(String tempToken) {
        Long userId = tempTokenProvider.resolveUserId(tempToken);
        tempTokenProvider.invalidateTempToken(tempToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException(NO_SUCH_USER));

        CustomUserDetails userDetails = CustomUserDetails.from(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        // AuthResponse 반환
        return new AuthResponse(jwtTokenProvider.createToken(authentication), buildUserResponse(user));
    }

    private long getExpiredAccessTokenTime(String token) {
        return jwtTokenProvider.getExpiredTime(token) - System.currentTimeMillis();
    }

    private String validateAccessToken(HttpServletRequest request) {
        String token = jwtTokenAuthenticationFilter.resolveToken(request);
        jwtTokenProvider.validateToken(token);
        log.trace("");
        return token;
    }

    private Authentication authenticateUser(LoginRequest authRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.loginId(), authRequest.password()));
    }

    private CustomUserDetails getCustomUserDetails(Authentication authenticate) {
        return (CustomUserDetails) authenticate.getPrincipal();
    }

    private void validateUser(User user) {
        if (!user.isActive()) {
            throw new NoSuchUserException(DELETED_MEMBER);
        }
    }

    private User getUser(CustomUserDetails customUserDetails) {
        return userRepository.findUserById(customUserDetails.getUserId())
                .orElseThrow(() -> new NoSuchUserException(NO_SUCH_USER));
    }

    private UserResponse buildUserResponse(User user) {
        List<UserDepartment> byUserId = userDepartmentRepository.findByUserId(user.getId());
        List<DepartmentResponse> list = byUserId.stream()
                .map(UserDepartment::getDepartment)
                .map(DepartmentResponse::of)
                .toList();

        return UserResponse.of(
                user.getId(),
                user.getOauthId(),
                user.getLoginId(),
                user.getEmail(),
                user.getNickname(),
                user.getStudentId(),
                user.getImageUrl(),
                list);
    }
}
