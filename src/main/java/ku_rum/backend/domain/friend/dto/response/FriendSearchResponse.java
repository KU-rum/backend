package ku_rum.backend.domain.friend.dto.response;

public record FriendSearchResponse(Long userId, String nickname, String imageUrl, boolean requestSent, boolean requestReceived, boolean isFriend) {
}
