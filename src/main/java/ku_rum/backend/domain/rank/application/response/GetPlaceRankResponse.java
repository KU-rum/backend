package ku_rum.backend.domain.rank.application.response;

import java.util.Comparator;
import java.util.List;
import ku_rum.backend.domain.rank.dto.PlaceRankWithRankingProjection;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;

public record GetPlaceRankResponse(int ranking, List<String> nickname, int sharingCount, boolean isSelf) {

    public static GetPlaceRankResponse from(List<PlaceRankWithRankingProjection> placeRanks, User user) {
        validatePlaceRanks(placeRanks);

        List<String> names = extractSortedNicknames(placeRanks);

        PlaceRankWithRankingProjection firstRank = placeRanks.get(0);

        boolean isSelf = containsUserNickname(placeRanks, user);

        return new GetPlaceRankResponse(firstRank.getRanking(), names, firstRank.getCount(), isSelf);
    }

    private static void validatePlaceRanks(List<PlaceRankWithRankingProjection> placeRanks) {
        if (placeRanks.isEmpty()) {
            throw new GlobalException(BaseExceptionResponseStatus.RANK_NOT_FOUND);
        }
    }

    private static List<String> extractSortedNicknames(List<PlaceRankWithRankingProjection> placeRanks) {
        return placeRanks.stream()
                .sorted(Comparator.comparing(PlaceRankWithRankingProjection::getModifiedAt))
                .map(PlaceRankWithRankingProjection::getNickname)
                .toList();
    }

    private static boolean containsUserNickname(List<PlaceRankWithRankingProjection> placeRanks, User user) {
        return placeRanks.stream()
                .anyMatch(rank -> rank.getNickname().equals(user.getNickname()));
    }
}
