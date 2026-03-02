package ku_rum.backend.domain.place.application.response;

import java.math.BigDecimal;
import java.util.List;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.PlaceImage;
import ku_rum.backend.domain.place.dto.FriendUserDto;
import ku_rum.backend.domain.rank.application.response.PlaceUserRankResponse;
import lombok.Builder;

@Builder
public record GetPlaceResponse(Long placeId,
                               String name,
                               String subName,
                               String content,
                               BigDecimal latitude,
                               BigDecimal longitude,
                               List<SelectPlaceChipFriendListResponse> friends,
                               List<PlaceUserRankResponse> ranks,
                               List<String> imageUrls) {

    public static GetPlaceResponse of(Place place,
                                      List<FriendUserDto> friendUserDtos,
                                      List<PlaceImage> placeImages,
                                      List<PlaceUserRankResponse> placeRanks) {
        List<SelectPlaceChipFriendListResponse> selectPlaceChipFriendListResponses = friendUserDtos.stream()
                .map(SelectPlaceChipFriendListResponse::from)
                .toList();

        List<String> ImageUrls = placeImages.stream().map(PlaceImage::getImageUrl).toList();
        return new GetPlaceResponse(place.getPlaceId(), place.getName(), place.getSubName(), place.getContent(),
                place.getLatitude(), place.getLongitude(), selectPlaceChipFriendListResponses, placeRanks, ImageUrls);
    }
}
