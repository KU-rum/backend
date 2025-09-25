package ku_rum.backend.domain.user.dto.response;

import ku_rum.backend.domain.department.dto.DepartmentResponse;

import java.util.List;

public record UserResponse(Long id, String oauthId, String loginId, String email, String nickname, String studentId,
                           String imageUrl, List<DepartmentResponse> departmentResponse) {

    public static UserResponse of(Long id, String oauthId, String loginId, String email, String nickname, String studentId, String imageUrl, List<DepartmentResponse> departmentResponse) {
        return new UserResponse(id, oauthId, loginId, email, nickname, studentId, imageUrl, departmentResponse);
    }
}
