package ku_rum.backend.domain.friend.presentation;

import java.util.List;
import ku_rum.backend.domain.friend.application.FriendQueryService;
import ku_rum.backend.domain.friend.dto.response.FriendListResponse;
import ku_rum.backend.domain.friend.dto.response.FriendSearchResponse;
import ku_rum.backend.domain.friend.dto.response.ReceivedFriendResponse;
import ku_rum.backend.domain.friend.dto.response.SentFriendResponse;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendQueryController {
    private final FriendQueryService friendQueryService;

    @GetMapping("/list")
    public BaseResponse<List<FriendListResponse>> getFriendList() {
        return BaseResponse.ok(friendQueryService.getFriendList());
    }

    @GetMapping("/requests/received")
    public BaseResponse<List<ReceivedFriendResponse>> getReceivedRequests() {
        return BaseResponse.ok(friendQueryService.getReceivedPendingRequests());
    }

    @GetMapping("/requests/sent")
    public BaseResponse<List<SentFriendResponse>> getSentRequests() {
        return BaseResponse.ok(friendQueryService.getSentPendingRequests());
    }

    @GetMapping("/search")
    public BaseResponse<List<FriendSearchResponse>> searchFriendByNickname(
            @RequestParam(name = "nickname") final String nickname) {
        return BaseResponse.ok(friendQueryService.searchByNickname(nickname));
    }
}
