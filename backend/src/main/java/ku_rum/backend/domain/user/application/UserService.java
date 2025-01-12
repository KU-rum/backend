package ku_rum.backend.domain.user.application;

import jakarta.validation.Valid;
import ku_rum.backend.domain.department.domain.Department;
import ku_rum.backend.domain.department.domain.repository.DepartmentRepository;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.domain.user.dto.request.mail.EmailValidationRequest;
import ku_rum.backend.domain.user.dto.request.UserSaveRequest;
import ku_rum.backend.domain.reservation.dto.request.WeinLoginRequest;
import ku_rum.backend.domain.user.dto.response.UserSaveResponse;
import ku_rum.backend.domain.user.dto.response.WeinLoginResponse;
import ku_rum.backend.global.exception.department.NoSuchDepartmentException;
import ku_rum.backend.global.exception.user.DuplicateEmailException;
import ku_rum.backend.global.exception.user.DuplicateStudentIdException;
import ku_rum.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import static ku_rum.backend.global.response.status.BaseExceptionResponseStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSaveResponse saveUser(final UserSaveRequest userSaveRequest) {
        validateDuplicateEmail(userSaveRequest.email());
        validateDuplicateStudentId(userSaveRequest.studentId());
        validateDepartmentName(userSaveRequest.department());

        String password = passwordEncoder.encode(userSaveRequest.password());
        Department department = departmentRepository.findByName(userSaveRequest.department())
               .orElseThrow(() -> new NoSuchDepartmentException(NO_SUCH_DEPARTMENT));

        User user = UserSaveRequest.newUser(userSaveRequest, department, password);

        return UserSaveResponse.from(userRepository.save(user));
    }

    public void validateEmail(final EmailValidationRequest emailValidationRequest) {
        validateDuplicateEmail(emailValidationRequest.email());
    }


    private void validateDuplicateEmail(final String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(DUPLICATE_EMAIL);
        }
    }

    private void validateDuplicateStudentId(final String studentId) {
        if (userRepository.existsByStudentId(studentId)) {
            throw new DuplicateStudentIdException(DUPLICATE_STUDENT_ID);
        }
    }

    private void validateDepartmentName(final String department) {
        if (!departmentRepository.existsByName(department)) {
            throw new NoSuchDepartmentException(NO_SUCH_DEPARTMENT);
        }
    }

    public BaseResponse<WeinLoginResponse> loginToWein(@Valid final WeinLoginRequest weinLoginRequest) {
        // 리다이렉션 전략과 쿠키 저장소를 설정하여 HttpClient 생성
        CloseableHttpClient httpClient = HttpClients.custom()
                .setRedirectStrategy(new DefaultRedirectStrategy()) // 기본 리다이렉션 전략
                .setDefaultCookieStore(new BasicCookieStore())       // 세션 유지를 위한 쿠키 관리
                .build();

        // HttpClient를 사용하는 RestTemplate 설정
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(createRequestBody(weinLoginRequest), headers);
        log.info("Attempting login with userId: {} and password: {}", weinLoginRequest.getUserId(), weinLoginRequest.getPassword());

        try {
            // 로그인 요청을 전송
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://wein.konkuk.ac.kr/common/user/loginProc.do",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            Optional<String> responseBodyOpt = Optional.ofNullable(response.getBody());
            if (responseBodyOpt.isPresent()) {
                String responseBody = responseBodyOpt.get();
                if (responseBody.contains("index.do")) {
                    log.info("Login successful for userId: {}", weinLoginRequest.getUserId());
                    return BaseResponse.ok(new WeinLoginResponse(true, "Wein login successful"));
                } else if (responseBody.contains("login.do")) {
                    log.warn("Login failed for userId: {}", weinLoginRequest.getUserId());
                    return BaseResponse.of(HttpStatus.UNAUTHORIZED, new WeinLoginResponse(false, "Invalid Wein credentials"));
                }
            }

            // 예상하지 못한 응답 처리
            log.error("Unexpected response for userId: {}, status code: {}, response body: {}", weinLoginRequest.getUserId(), response.getStatusCode(), response.getBody());
            return BaseResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, new WeinLoginResponse(false, "Unexpected login response"));

        } catch (Exception e) {
            log.error("Exception occurred during login: ", e);
            return BaseResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, new WeinLoginResponse(false, "Wein login failed due to server error"));
        }
    }

    private MultiValueMap<String, String> createRequestBody(WeinLoginRequest weinLoginRequest) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("userId", weinLoginRequest.getUserId());
        requestBody.add("pw", weinLoginRequest.getPassword());
        requestBody.add("rtnUrl", ""); // 리다이렉트 후 이동할 URL 지정, 필요시 수정
        return requestBody;
    }

}
