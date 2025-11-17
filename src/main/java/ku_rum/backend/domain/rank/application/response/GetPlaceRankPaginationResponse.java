package ku_rum.backend.domain.rank.application.response;

import java.util.List;
import lombok.Builder;


@Builder
public record GetPlaceRankPaginationResponse(List<GetPlaceRankResponse> ranks, boolean hasNext, String nextCursor) {
    public static GetPlaceRankPaginationResponse of(List<GetPlaceRankResponse> response, boolean hasNext,
                                                    String nextCursor) {
        return GetPlaceRankPaginationResponse.builder()
                .ranks(response)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }
}
