package ku_rum.backend.domain.user.application;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.DUPLICATE_DEPARTMENT;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.DUPLICATE_NICKNAME;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_DEPARTMENT;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_USER;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.PREV_NEW_EQUAL_EXCEPTION;

import java.util.List;
import ku_rum.backend.domain.auth.dto.response.AuthResponse;
import ku_rum.backend.domain.common.mail.application.MailService;
import ku_rum.backend.domain.department.application.DepartmentQueryService;
import ku_rum.backend.domain.department.application.UserDepartmentService;
import ku_rum.backend.domain.department.domain.Department;
import ku_rum.backend.domain.department.domain.UserDepartment;
import ku_rum.backend.domain.department.domain.repository.DepartmentRepository;
import ku_rum.backend.domain.department.domain.repository.UserDepartmentRepository;
import ku_rum.backend.domain.department.dto.DepartmentResponse;
import ku_rum.backend.domain.oauth.handler.PreSignupTokenProvider;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.domain.user.dto.request.InitiatePasswordResetRequest;
import ku_rum.backend.domain.user.dto.request.NicknameChangeRequest;
import ku_rum.backend.domain.user.dto.request.ProfileChangeRequest;
import ku_rum.backend.domain.user.dto.request.ResetPasswordRequest;
import ku_rum.backend.domain.user.dto.request.SocialSignupRequest;
import ku_rum.backend.domain.user.dto.request.TemporaryUserRequest;
import ku_rum.backend.domain.user.dto.request.UserSaveRequest;
import ku_rum.backend.domain.user.dto.response.LoginIdResponse;
import ku_rum.backend.domain.user.dto.response.TemporaryUserResponse;
import ku_rum.backend.domain.user.dto.response.TokenResponse;
import ku_rum.backend.domain.user.dto.response.UserResponse;
import ku_rum.backend.domain.user.dto.response.UserSaveResponse;
import ku_rum.backend.global.exception.department.DuplicateDepartmentException;
import ku_rum.backend.global.exception.department.NoSuchDepartmentException;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.exception.user.DuplicateNicknameException;
import ku_rum.backend.global.exception.user.NoSuchUserException;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.security.JwtTokenProvider;
import ku_rum.backend.global.utill.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final UserQueryService userQueryService;
    private final DepartmentQueryService departmentQueryService;
    private final UserDepartmentRepository userDepartmentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserDepartmentService userDepartmentService;
    private final MailService mailService;
    private final PreSignupTokenProvider preSignupTokenProvider;
    private final JwtTokenProvider jwtTokenProvider;

    // 소셜 로그인 X
    @Transactional
    public UserSaveResponse saveUser(final UserSaveRequest userSaveRequest) {
        log.info("사용자 저장 요청: {}", userSaveRequest);
        userValidator.validateUser(userSaveRequest);

        Department department = departmentQueryService.getDepartment(userSaveRequest);

        User user = UserSaveRequest.newUser(userSaveRequest, passwordEncoder.encode(userSaveRequest.password()));
        userDepartmentService.addDeptToUser(user, department);
        user.changeFirstLogin(false);

        log.info("사용자 저장 완료: ID={}", userSaveRequest.loginId());
        return UserSaveResponse.from(userRepository.save(user));
    }

    /**
     * 프리사인업 토큰으로 소셜 가입 완료 + 즉시 로그인(JWT 발급)
     */
    @Transactional
    public AuthResponse completeSocialSignup(final SocialSignupRequest req) {
        log.info("소셜 가입 요청: {}", req);

        PreSignupTokenProvider.PreSignupPayload payload = preSignupTokenProvider.resolve(req.token());

        userRepository.findByOauthId(payload.getOauthId()).ifPresent(u -> {
            throw new BadCredentialsException("이미 가입된 계정입니다. 로그인해주세요.");
        });

        Department department = departmentQueryService.getDepartment(req.department());

        User user = User.builder()
                .providerType(payload.getProviderType())
                .oauthId(payload.getOauthId())
                .studentId(req.studentId())
                .nickname(req.nickname())
                .agreementStatus(req.agreementStatus())
                .build();
        user.changeFirstLogin(false);
        user = userRepository.save(user);

        userDepartmentService.addDeptToUser(user, department);

        preSignupTokenProvider.invalidate(req.token());

        CustomUserDetails userDetails = CustomUserDetails.from(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        log.info("소셜 가입 완료: userId={}", user.getId());
        return AuthResponse.of(jwtTokenProvider.createToken(authentication), buildUserResponse(user));
    }

    @Transactional
    public void initiatePasswordReset(final InitiatePasswordResetRequest initiatePasswordResetRequest) {
        log.info("계정 초기화 요청: loginId={}", initiatePasswordResetRequest.loginId());
        User user = userQueryService.getUserByLoginId(initiatePasswordResetRequest.loginId());
        mailService.verifyCode(initiatePasswordResetRequest.emailRequest());

        if (passwordEncoder.matches(initiatePasswordResetRequest.newPassword(), user.getPassword())) {
            throw new GlobalException(PREV_NEW_EQUAL_EXCEPTION);
        }

        user.changePassword(passwordEncoder.encode(initiatePasswordResetRequest.newPassword()));
        log.info("계정 비밀번호 변경 완료: loginId={}", initiatePasswordResetRequest.loginId());
    }

    @Transactional
    public void resetPassword(final ResetPasswordRequest resetPasswordRequest) {
        log.info("기존 계정 비밀번호 변경 요청");
        User user = getUser();
        userValidator.validatePassword(resetPasswordRequest.prevPassword(), user.getPassword());

        if (resetPasswordRequest.newPassword().equals(resetPasswordRequest.prevPassword())) {
            throw new GlobalException(PREV_NEW_EQUAL_EXCEPTION);
        }
        user.changePassword(passwordEncoder.encode(resetPasswordRequest.newPassword()));
        log.info("계정 비밀번호 변경 완료: loginId={}", user.getLoginId());
    }

    @Transactional
    public void changeProfile(final ProfileChangeRequest profileChangeRequest) {
        log.info("프로필 변경 요청: imageUrl={}", profileChangeRequest.imageUrl());
        User user = getUser();
        user.changeImage(profileChangeRequest.imageUrl());
        log.info("프로필 변경 완료: userId={}", user.getId());
    }

    public LoginIdResponse getLoginId(final String email) {
        log.info("로그인 ID 조회 요청: email={}", email);
        User user = userQueryService.getUserByEmail(email);
        log.info("로그인 ID 조회 완료: loginId={}", user.getLoginId());
        return LoginIdResponse.of(user.getLoginId());

    }

    @Transactional
    public void changeNickname(final NicknameChangeRequest nicknameChangeRequest) {
        User user = getUser();

        if (isNicknameDuplicate(nicknameChangeRequest.nickname())) {
            throw new DuplicateNicknameException(DUPLICATE_NICKNAME);
        }
        user.changeNickname(nicknameChangeRequest.nickname());
    }

    public User getUser() {
        Long memberId = UserUtil.getLongMemberId();
        log.debug("현재 사용자 조회: userId={}", memberId);
        return userRepository.findUserById(memberId).orElseThrow(() -> new NoSuchUserException(NO_SUCH_USER));
    }

    @Transactional
    public void deactivate() {
        User user = getUser();
        log.info("사용자 탈퇴 명령");
        userRepository.delete(user);
    }

    @Transactional
    public void addDepartment(final String department) {
        User user = getUser();
        Department departmentEntity = departmentRepository.findFirstByName(department)
                .orElseThrow(() -> new NoSuchDepartmentException(NO_SUCH_DEPARTMENT));
        if (userDepartmentRepository.existsByUserIdAndDepartmentId(user.getId(), departmentEntity.getId())) {
            throw new DuplicateDepartmentException(DUPLICATE_DEPARTMENT);
        }
        userDepartmentRepository.save(UserDepartment.of(user, departmentEntity));
    }

    @Transactional
    public void deleteDepartment(final String department) {
        Department dept = departmentRepository.findFirstByName(department)
                .orElseThrow(() -> new NoSuchDepartmentException(NO_SUCH_DEPARTMENT));

        int deleted = userDepartmentRepository.deleteByUserIdAndDepartmentId(
                getUser().getId(), dept.getId()
        );

        if (deleted == 0) {
            throw new NoSuchDepartmentException(NO_SUCH_DEPARTMENT);
        }
    }

    private boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
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

    public TemporaryUserResponse getUserToken(TemporaryUserRequest request) {
        User user = userRepository.findUserById(request.userId())
                .orElseThrow(() -> new GlobalException(NO_SUCH_USER));
        CustomUserDetails userDetails = CustomUserDetails.from(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        TokenResponse token = jwtTokenProvider.createToken(authentication);
        return TemporaryUserResponse.from(token);
    }
}
