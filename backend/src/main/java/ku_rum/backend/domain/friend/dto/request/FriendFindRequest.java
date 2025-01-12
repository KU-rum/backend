package ku_rum.backend.domain.friend.dto.request;

import lombok.Builder;

public record FriendFindRequest(
        Long userId,
        String nickname) {

    @Builder
    public FriendFindRequest {}

    public static FriendFindRequest of(Long userId, String nickname) {
        return FriendFindRequest
                .builder()
                .userId(userId)
                .nickname(nickname)
                .build();
    }
}
