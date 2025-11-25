package ku_rum.backend.domain.place.presentation;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.place.application.PlaceHistoryService;
import ku_rum.backend.domain.place.application.PlaceService;
import ku_rum.backend.domain.place.application.PositionService;
import ku_rum.backend.domain.place.application.RankingChangeDto;
import ku_rum.backend.domain.place.application.response.CurrentPositionConfirmResponse;
import ku_rum.backend.domain.place.application.response.CurrentPositionResponse;
import ku_rum.backend.domain.place.application.response.CurrentPositionStatusResponse;
import ku_rum.backend.domain.place.application.response.GetPlaceResponse;
import ku_rum.backend.domain.place.application.response.SearchPlaceHistoryResponse;
import ku_rum.backend.domain.place.application.response.SearchPlaceResponse;
import ku_rum.backend.domain.place.application.response.SelectPlaceChipResponse;
import ku_rum.backend.domain.place.domain.CategoryChip;
import ku_rum.backend.domain.place.dto.request.CurrentPositionConfirmRequest;
import ku_rum.backend.domain.place.dto.request.CurrentPositionRequest;
import ku_rum.backend.domain.rank.application.RankService;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PositionService positionService;
    private final PlaceService placeService;
    private final PlaceHistoryService placeHistoryService;
    private final RankService rankService;

    @GetMapping("/sharing/status")
    public BaseResponse<CurrentPositionStatusResponse> getCurrentPositionStatus(
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        CurrentPositionStatusResponse response = positionService.getCurrentPositionStatus(userDetails);
        return BaseResponse.ok(response);
    }

    @PostMapping("/sharing")
    public BaseResponse<CurrentPositionResponse> getCurrentPosition(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @RequestBody CurrentPositionRequest request) {
        CurrentPositionResponse response = positionService.getCurrentPosition(userDetails, request);
        return BaseResponse.ok(response);
    }

    @PostMapping("/sharing/confirm")
    public BaseResponse<CurrentPositionConfirmResponse> sharingPosition(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @RequestBody CurrentPositionConfirmRequest request) {
        CurrentPositionConfirmResponse response = positionService.confirmCurrentPosition(userDetails, request);

        positionService.alarmConfirmCurrentPosition(userDetails, response);
        return BaseResponse.ok(response);
    }

    @DeleteMapping("/sharing/confirm")
    public BaseResponse<Void> disableSharingPosition(@AuthenticationPrincipal final CustomUserDetails userDetails) {
        Optional<RankingChangeDto> rankingChangeDtoOptional = positionService.disableSharingPosition(userDetails);
        if (rankingChangeDtoOptional.isEmpty()) {
            return BaseResponse.ok();
        }
        RankingChangeDto rankingChangeDto = rankingChangeDtoOptional.get();
        rankService.checkoutRankChange(rankingChangeDto, userDetails);
        return BaseResponse.ok();
    }

    /**
     * 지도 침 조회
     *
     * @param userDetails 인증 객체(없을 경우 비회원 로직)
     * @return 칩에 해당하는 데이터 리스트
     */
    @GetMapping
    public BaseResponse<List<SelectPlaceChipResponse>> selectChip(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @RequestParam("chip") final CategoryChip category) {
        if (isAuthenticated(userDetails)) {
            return BaseResponse.ok(placeService.selectChipWithUser(userDetails, category));
        }
        return BaseResponse.ok(placeService.selectChip(category));
    }

    @GetMapping("/{placeId}")
    public BaseResponse<GetPlaceResponse> getPlace(@PathVariable("placeId") final Long placeId,
                                                   @AuthenticationPrincipal final CustomUserDetails userDetails) {
        if (isAuthenticated(userDetails)) {
            return BaseResponse.ok(placeService.getPlaceWithUser(userDetails, placeId));
        }
        return BaseResponse.ok(placeService.getPlace(placeId));
    }

    @GetMapping("/search")
    public BaseResponse<List<SearchPlaceResponse>> searchPlace(@RequestParam("query") String query,
                                                               @AuthenticationPrincipal final CustomUserDetails userDetails) {
        if (isAuthenticated(userDetails)) {
            return BaseResponse.ok(placeService.searchPlaceWithUser(userDetails, query));
        }
        return BaseResponse.ok(placeService.searchPlace(query));
    }

    @GetMapping("/search/history")
    public BaseResponse<List<SearchPlaceHistoryResponse>> searchPlaceHistory(
            @AuthenticationPrincipal final CustomUserDetails userDetails) {
        return BaseResponse.ok(placeHistoryService.searchPlaceHistory(userDetails));
    }

    @DeleteMapping("/search/history/{placeHistoryId}")
    public BaseResponse<Void> deletePlaceHistory(@PathVariable("placeHistoryId") Long placeHistoryId,
                                                 @AuthenticationPrincipal final CustomUserDetails userDetails) {
        placeHistoryService.deletePlaceHistory(placeHistoryId, userDetails);
        return BaseResponse.ok();
    }

    @DeleteMapping("/search/history")
    public BaseResponse<Void> deletePlaceHistory(@AuthenticationPrincipal final CustomUserDetails userDetails) {
        placeHistoryService.deletePlaceHistory(userDetails);
        return BaseResponse.ok();
    }

    private boolean isAuthenticated(final CustomUserDetails userDetails) {
        if (userDetails == null) {
            return false;
        }
        return true;
    }
}