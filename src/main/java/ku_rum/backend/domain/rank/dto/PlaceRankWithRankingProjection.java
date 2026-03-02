package ku_rum.backend.domain.rank.dto;

import java.time.LocalDateTime;

public interface PlaceRankWithRankingProjection {
    Long getRankId();

    String getNickname();

    Long getPlacePlaceId();

    int getCount();

    LocalDateTime getCreatedAt();

    LocalDateTime getModifiedAt();

    int getRanking();
}
