package ku_rum.backend.domain.place.application.response;

import ku_rum.backend.domain.place.domain.Place;

public record SearchPlaceResponse(String name, Long placeId) {

    public static SearchPlaceResponse from(Place place) {
        return new SearchPlaceResponse(place.getName(), place.getPlaceId());
    }
}
