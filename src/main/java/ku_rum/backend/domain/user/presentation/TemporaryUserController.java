package ku_rum.backend.domain.user.presentation;

import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.dto.request.TemporaryUserRequest;
import ku_rum.backend.domain.user.dto.response.TemporaryUserResponse;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class TemporaryUserController {

    private final UserService userService;

    @PostMapping("/temporary")
    public BaseResponse<TemporaryUserResponse> getUserToken(@RequestBody TemporaryUserRequest request) {
        TemporaryUserResponse response = userService.getUserToken(request);
        return BaseResponse.ok(response);
    }
}
