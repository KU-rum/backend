package ku_rum.backend.domain.rank.application.response;

import java.util.List;
import ku_rum.backend.domain.rank.dto.PlaceRankWithRankingProjection;

public record GetPlaceTopRankResponse(int ranking, List<String> nickname, int sharingCount) {
    private static final int EXCEPTION_RANKING = -1;
    private static final int EXCEPTION_COUNT_RANKING = 0;

    public static GetPlaceTopRankResponse from(List<PlaceRankWithRankingProjection> placeRankWithRankingProjections) {
        if (placeRankWithRankingProjections == null || placeRankWithRankingProjections.isEmpty()) {
            return new GetPlaceTopRankResponse(
                    EXCEPTION_RANKING,
                    List.of(),
                    EXCEPTION_COUNT_RANKING
            );
        }

        int ranking = placeRankWithRankingProjections.get(0).getRanking();

        List<String> nicknames = placeRankWithRankingProjections.stream()
                .map(PlaceRankWithRankingProjection::getNickname)
                .toList();

        int sharingCount = placeRankWithRankingProjections.get(0).getCount();

        return new GetPlaceTopRankResponse(
                ranking,
                nicknames,
                sharingCount
        );
    }
}
