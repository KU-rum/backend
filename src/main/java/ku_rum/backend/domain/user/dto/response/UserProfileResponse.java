package ku_rum.backend.domain.user.dto.response;

import java.util.List;

public record UserProfileResponse(String profileImage, String email, String loginId, String nickname, String studentId,
                                  List<UserProfileDepartmentResponse> departments) {
}
