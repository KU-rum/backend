package ku_rum.backend.domain.rank.dto;

import java.time.LocalDateTime;

public class PlaceRankWithRankingDto {
    private Long rankId;
    private Long userId;
    private Long placePlaceId;
    private int count;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int ranking;

    // 생성자
    public PlaceRankWithRankingDto(Long rankId, Long userId, Long placePlaceId, int count,
                                   LocalDateTime createdAt, LocalDateTime modifiedAt, int ranking) {
        this.rankId = rankId;
        this.userId = userId;
        this.placePlaceId = placePlaceId;
        this.count = count;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.ranking = ranking;
    }

    // getters
}
