package ku_rum.backend.domain.friend.dto.request;

public record FriendListRequest(Long userId){
    public static FriendListRequest from(Long userId) {
        return new FriendListRequest(userId);
    }
}
