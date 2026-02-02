package ku_rum.backend.domain.place.dto.response;

import ku_rum.backend.domain.place.domain.PlaceImage;

public record GetPlaceImageResponse(Long placeImageId, String imageUrl) {

    public static GetPlaceImageResponse from(PlaceImage placeImage) {
        return new GetPlaceImageResponse(placeImage.getPlaceImageId(), placeImage.getImageUrl());
    }
}
