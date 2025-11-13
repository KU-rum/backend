package ku_rum.backend.domain.rank.application.response;

import ku_rum.backend.domain.rank.dto.PlaceRankWithRankingProjection;
import ku_rum.backend.domain.user.domain.User;

public record GetPlaceRankResponse(int ranking, String nickname, int sharingCount) {

    public static GetPlaceRankResponse from(PlaceRankWithRankingProjection placeRanks) {

        return new GetPlaceRankResponse(placeRanks.getRanking(), placeRanks.getNickname(), placeRanks.getCount());
    }

    public static GetPlaceRankResponse emptyFrom(User user) {
        return new GetPlaceRankResponse(-1, user.getNickname(), 0);

    }
}
