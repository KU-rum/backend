package ku_rum.backend.domain.rank.application.response;

import java.util.Comparator;
import java.util.List;
import ku_rum.backend.domain.rank.dto.PlaceRankWithRankingProjection;
import ku_rum.backend.domain.user.domain.User;

public record PlaceUserRankResponse(int ranking, List<String> nickname, int sharingCount, boolean isSelf) {

    public static PlaceUserRankResponse from(List<PlaceRankWithRankingProjection> placeRanks, User user) {
        List<String> names = placeRanks.stream()
                .sorted(Comparator.comparing(PlaceRankWithRankingProjection::getModifiedAt))
                .map(placeRankWithRankingProjection -> placeRankWithRankingProjection.getNickname())
                .toList();

        int count = placeRanks.stream()
                .findFirst()
                .get()
                .getCount();

        boolean isSelf = placeRanks.stream()
                .findFirst()
                .map(placeRankWithRankingProjection -> placeRankWithRankingProjection.getNickname()
                        .equals(user.getNickname()))
                .orElse(false);

        return new PlaceUserRankResponse(placeRanks.get(0).getRanking(), names, count, isSelf);
    }
}
