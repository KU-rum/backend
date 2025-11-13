package ku_rum.backend.domain.rank.application.response;

import ku_rum.backend.domain.rank.dto.PlaceRankWithRankingProjection;

public record GetPlaceRankResponse(int ranking, String nickname, int sharingCount) {

    public static GetPlaceRankResponse from(PlaceRankWithRankingProjection placeRanks) {

        return new GetPlaceRankResponse(placeRanks.getRanking(), placeRanks.getNickname(), placeRanks.getCount());
    }
}
