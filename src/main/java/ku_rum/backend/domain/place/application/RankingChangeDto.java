package ku_rum.backend.domain.place.application;

import ku_rum.backend.domain.rank.domain.PlaceRank;

public record RankingChangeDto(int beforeRank, int afterRank, PlaceRank placeRank) {
}
