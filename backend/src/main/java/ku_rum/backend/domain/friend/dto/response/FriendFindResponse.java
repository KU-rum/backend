package ku_rum.backend.domain.friend.dto.response;

import ku_rum.backend.domain.user.domain.User;
import lombok.Builder;

public record FriendFindResponse(Long id, String nickname) {

    @Builder
    public FriendFindResponse {
    }

    public static FriendFindResponse from(User user) {
        return FriendFindResponse.builder().id(user.getId()).nickname(user.getNickname()).build();
    }
}
