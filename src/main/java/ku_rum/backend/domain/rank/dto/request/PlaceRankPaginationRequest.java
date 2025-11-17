package ku_rum.backend.domain.rank.dto.request;

import jakarta.validation.constraints.Min;

public record PlaceRankPaginationRequest(String lastKnown,
                                         @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
                                         int limit) {

}
