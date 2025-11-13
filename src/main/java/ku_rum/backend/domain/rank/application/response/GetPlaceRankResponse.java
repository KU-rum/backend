package ku_rum.backend.domain.rank.application.response;

import java.util.Comparator;
import java.util.List;
import ku_rum.backend.domain.rank.dto.PlaceRankWithRankingProjection;
import ku_rum.backend.domain.user.domain.User;

public record GetPlaceRankResponse(int ranking, String nickname, int sharingCount) {

    public static GetPlaceRankResponse from(PlaceRankWithRankingProjection placeRanks, User user) {

        return new GetPlaceRankResponse(placeRanks.getRanking(), placeRanks.getNickname(), placeRanks.getCount());
    }

    private static List<String> extractSortedNicknames(List<PlaceRankWithRankingProjection> placeRanks) {
        return placeRanks.stream()
                .sorted(Comparator.comparing(PlaceRankWithRankingProjection::getModifiedAt))
                .map(PlaceRankWithRankingProjection::getNickname)
                .toList();
    }
}
