package ku_rum.backend.domain.place.presentation;

import ku_rum.backend.domain.place.application.PlaceService;
import ku_rum.backend.domain.place.dto.request.PutPlaceContentRequest;
import ku_rum.backend.domain.place.dto.request.PutPlaceSubNameRequest;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceAdminController {

    private final PlaceService placeService;

    @PatchMapping("/{placeId}/sub-name")
    public BaseResponse<Void> modifyPlaceSubName(@PathVariable("placeId") final Long placeId,
                                                 @RequestBody PutPlaceSubNameRequest request) {
        placeService.modifyPlaceSubName(placeId, request);
        return BaseResponse.ok();
    }

    @PatchMapping("/{placeId}/content")
    public BaseResponse<Void> modifyPlaceContent(@PathVariable("placeId") final Long placeId,
                                                 @RequestBody PutPlaceContentRequest request) {
        placeService.modifyPlaceContent(placeId, request);
        return BaseResponse.ok();
    }
}
