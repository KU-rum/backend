package ku_rum.backend.domain.place.application.response;

import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.PlaceHistory;

public record SearchPlaceHistoryResponse(String name, Long placeId) {

    public static SearchPlaceHistoryResponse from(PlaceHistory placeHistory) {
        Place place = placeHistory.getPlace();
        return new SearchPlaceHistoryResponse(place.getName(), place.getPlaceId());
    }
}
