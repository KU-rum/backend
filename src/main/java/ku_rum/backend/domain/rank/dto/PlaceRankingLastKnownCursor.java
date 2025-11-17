package ku_rum.backend.domain.rank.dto;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.INVALID_LAST_KNOWN;
import ku_rum.backend.global.exception.global.GlobalException;

public record PlaceRankingLastKnownCursor(int lastRank, Long lastRankId) {

    private static final String DELIMITER = "_"; // 구분자, 예: "10_12345"

    public static PlaceRankingLastKnownCursor from(String lastKnown) {
        if (lastKnown == null || lastKnown.isBlank()) {
            return new PlaceRankingLastKnownCursor(0, 0L);
        }

        String[] parts = lastKnown.split(DELIMITER);
        if (parts.length != 2) {
            throw new GlobalException(INVALID_LAST_KNOWN);
        }

        try {
            int lastRank = Integer.parseInt(parts[0]);
            Long lastRankId = Long.parseLong(parts[1]);
            return new PlaceRankingLastKnownCursor(lastRank, lastRankId);
        } catch (NumberFormatException e) {
            throw new GlobalException(INVALID_LAST_KNOWN);
        }
    }

    public static PlaceRankingLastKnownCursor of(int lastRank, Long lastRankId) {
        return new PlaceRankingLastKnownCursor(lastRank, lastRankId);
    }

    public String toCursorString() {
        return lastRank + DELIMITER + lastRankId;
    }
}