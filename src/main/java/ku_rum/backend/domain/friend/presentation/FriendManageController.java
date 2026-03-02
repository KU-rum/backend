package ku_rum.backend.domain.friend.presentation;

import ku_rum.backend.domain.friend.application.FriendManageService;
import ku_rum.backend.domain.friend.dto.request.FriendRequest;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendManageController {

    private final FriendManageService friendManageService;

    /**
     * 친구 요청 API
     */
    @PostMapping("/request")
    public BaseResponse<Void> sendRequest(@RequestBody final FriendRequest friendSendRequest) {
        friendManageService.requestFriend(friendSendRequest);
        return BaseResponse.ok();
    }

    /**
     * 친구 요청 수락 API
     */
    @PutMapping("/accept")
    public BaseResponse<Void> acceptRequest(@RequestBody final FriendRequest friendAcceptRequest) {
        friendManageService.respondToFriend(friendAcceptRequest);
        return BaseResponse.ok();
    }

    /**
     * 친구 요청 거절 API
     */
    @PutMapping("/reject")
    public BaseResponse<Void> rejectRequest(@RequestBody final FriendRequest friendRejectRequest) {
        friendManageService.rejectToFriend(friendRejectRequest);
        return BaseResponse.ok();
    }

    /**
     * 보낸 친구 요청 삭제 API
     */
    @DeleteMapping("/request")
    public BaseResponse<Void> deleteSentRequest(@RequestBody final FriendRequest FriendDeleteRequest) {
        friendManageService.deleteSentRequest(FriendDeleteRequest);
        return BaseResponse.ok();
    }

    /**
     * 친구 삭제 API
     */
    @DeleteMapping("/{friendId}")
    public BaseResponse<Void> deleteFriend(@PathVariable final Long friendId) {
        friendManageService.deleteFriend(friendId);
        return BaseResponse.ok();
    }
}
