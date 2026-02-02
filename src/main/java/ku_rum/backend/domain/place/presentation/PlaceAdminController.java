package ku_rum.backend.domain.place.presentation;

import ku_rum.backend.domain.place.application.PlaceService;
import ku_rum.backend.domain.place.dto.request.DeletePlaceImagesRequest;
import ku_rum.backend.domain.place.dto.request.PostPlaceRequest;
import ku_rum.backend.domain.place.dto.request.PutPlaceContentRequest;
import ku_rum.backend.domain.place.dto.request.PutPlaceImagesRequest;
import ku_rum.backend.domain.place.dto.request.PutPlaceLocationRequest;
import ku_rum.backend.domain.place.dto.request.PutPlaceSubNameRequest;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PatchMapping("/{placeId}/location")
    public BaseResponse<Void> modifyPlaceLocation(@PathVariable("placeId") final Long placeId,
                                                  @RequestBody PutPlaceLocationRequest request) {
        placeService.modifyPlaceLocation(placeId, request);
        return BaseResponse.ok();
    }

    @PutMapping(value = "/{placeId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> modifyPlaceImages(@PathVariable("placeId") final Long placeId,
                                                @ModelAttribute PutPlaceImagesRequest request) {
        placeService.modifyPlaceImages(placeId, request.images());
        return BaseResponse.ok();
    }

    @PatchMapping(value = "/{placeId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> addPlaceImages(@PathVariable("placeId") final Long placeId,
                                             @ModelAttribute PutPlaceImagesRequest request) {
        placeService.addPlaceImages(placeId, request.images());
        return BaseResponse.ok();
    }

    @DeleteMapping("/{placeId}/images")
    public BaseResponse<Void> deletePlaceImages(@PathVariable("placeId") final Long placeId,
                                                @RequestBody DeletePlaceImagesRequest request) {
        placeService.deletePlaceImages(placeId, request.imageUrls());
        return BaseResponse.ok();
    }

    @PostMapping
    public BaseResponse<Void> createPlace(@RequestBody PostPlaceRequest request) {
        placeService.createPlace(request);
        return BaseResponse.ok();
    }
}
