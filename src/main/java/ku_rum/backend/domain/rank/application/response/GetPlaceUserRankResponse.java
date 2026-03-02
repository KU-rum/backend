package ku_rum.backend.domain.rank.application.response;

import java.util.Comparator;
import java.util.List;
import ku_rum.backend.domain.rank.domain.PlaceRank;

public record GetPlaceUserRankResponse(List<String> name, int sharingCount) {

    public static GetPlaceUserRankResponse from(List<PlaceRank> placeRanks) {
        List<String> names = placeRanks.stream()
                .sorted(Comparator.comparing(PlaceRank::getModifiedAt))
                .map(placeRank -> placeRank.getPlace().getName())
                .toList();
        int count = placeRanks.stream()
                .findFirst()
                .get()
                .getCount();

        return new GetPlaceUserRankResponse(names, count);
    }
}
