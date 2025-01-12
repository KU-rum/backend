package ku_rum.backend.domain.user.dto.response;

import ku_rum.backend.domain.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveResponse {
    private Long id;

    private UserSaveResponse(Long id) {
        this.id = id;
    }

    public static UserSaveResponse from(User user) {
        return new UserSaveResponse(user.getId());
    }

}
