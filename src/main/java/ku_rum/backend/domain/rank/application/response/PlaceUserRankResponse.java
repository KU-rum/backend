package ku_rum.backend.domain.rank.application.response;

import java.util.Comparator;
import java.util.List;
import ku_rum.backend.domain.rank.domain.PlaceRank;
import ku_rum.backend.domain.user.domain.User;

public record PlaceUserRankResponse(List<String> name, int sharingCount, boolean isSelf) {

    public static PlaceUserRankResponse from(List<PlaceRank> placeRanks, User user) {
        List<String> names = placeRanks.stream()
                .sorted(Comparator.comparing(PlaceRank::getModifiedAt))
                .map(placeRank -> placeRank.getPlace().getName())
                .toList();

        int count = placeRanks.stream()
                .findFirst()
                .get()
                .getCount();

        boolean isSelf = placeRanks.stream()
                .findFirst()
                .map(placeRank -> placeRank.getUser().getNickname().equals(user.getNickname()))
                .orElse(false);

        return new PlaceUserRankResponse(names, count, isSelf);
    }
}
