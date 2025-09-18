package ku_rum.backend.domain.place.dto.response;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.PlaceImage;

public record SelectPlaceChipResponse(String name,
                                      String subName,
                                      String content,
                                      BigDecimal latitude,
                                      BigDecimal longitude,
                                      List<String> imgUrls,
                                      List<SelectPlaceChipFriendListResponse> friends
) {

    public static SelectPlaceChipResponse from(Place place, Map<Long, List<PlaceImage>> placeImageMap) {
        List<String> imgUrls = Optional.ofNullable(placeImageMap.get(place.getPlaceId()))
                .orElse(List.of())
                .stream()
                .map(PlaceImage::getImageUrl)
                .toList();
        return new SelectPlaceChipResponse(place.getName(), place.getSubName(), place.getContent(), place.getLatitude(),
                place.getLongitude(), imgUrls, Collections.emptyList());
    }

    public static SelectPlaceChipResponse from(Place place,
                                               List<SelectPlaceChipFriendListResponse> selectPlaceChipFriendListResponses,
                                               Map<Long, List<PlaceImage>> placeImageMap) {
        List<String> imgUrls = Optional.ofNullable(placeImageMap.get(place.getPlaceId()))
                .orElse(List.of())
                .stream()
                .map(PlaceImage::getImageUrl)
                .toList();
        return new SelectPlaceChipResponse(place.getName(), place.getSubName(), place.getContent(), place.getLatitude(),
                place.getLongitude(), imgUrls, selectPlaceChipFriendListResponses);
    }
}
