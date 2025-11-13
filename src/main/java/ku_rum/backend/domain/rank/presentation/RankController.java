package ku_rum.backend.domain.rank.presentation;

import java.util.List;
import ku_rum.backend.domain.rank.application.RankService;
import ku_rum.backend.domain.rank.application.response.GetPlaceRankPaginationResponse;
import ku_rum.backend.domain.rank.application.response.GetPlaceUserRankResponse;
import ku_rum.backend.domain.rank.dto.request.PlaceRankPaginationRequest;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @GetMapping("/users/ranks")
    public BaseResponse<List<GetPlaceUserRankResponse>> getPlaceUserRank(
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        return BaseResponse.ok(rankService.getPlaceUserRank(userDetails));
    }

    @GetMapping("/users/{friendId}/ranks")
    public BaseResponse<List<GetPlaceUserRankResponse>> getPlaceFriendRank(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @PathVariable("friendId") final Long friendId) {
        return BaseResponse.ok(rankService.getPlaceFriendRank(userDetails, friendId));
    }

    @GetMapping("/{placeId}/ranks")
    public BaseResponse<GetPlaceRankPaginationResponse> getPlaceRank(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @PathVariable("placeId") final Long placeId,
            @RequestParam PlaceRankPaginationRequest request){
        rankService.getPlaceRanks(userDetails.getUserId(),placeId,request)
        return BaseResponse.ok(rankService.getPlaceRanks(userDetails, placeId, placeRankPaginationRequest));
    }
}
