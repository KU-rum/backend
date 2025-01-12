package ku_rum.backend.domain.friend.dto.response;

import ku_rum.backend.domain.friend.domain.Friend;
import lombok.Builder;

public record FriendListResponse(
        Long id,
        String name) {

    @Builder
    public FriendListResponse {
    }

    public static FriendListResponse from(Friend friend) {
        return FriendListResponse.builder()
                .id(friend.getToUser().getId())
                .name(friend.getToUser().getNickname())
                .build();
    }
}
