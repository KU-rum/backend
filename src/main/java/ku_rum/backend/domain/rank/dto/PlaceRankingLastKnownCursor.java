package ku_rum.backend.domain.rank.dto;

public record PlaceRankingLastKnownCursor(int lastRank, Long lastRankId) {

    private static final String DELIMITER = "_"; // 구분자, 예: "10_12345"

    public static PlaceRankingLastKnownCursor from(String lastKnown) {
        if (lastKnown == null || lastKnown.isBlank()) {
            return new PlaceRankingLastKnownCursor(0, 0L);
        }

        String[] parts = lastKnown.split(DELIMITER);
        if (parts.length != 2) {
            throw new IllegalArgumentException("잘못된 lastKnown" + lastKnown);
        }

        try {
            int lastRank = Integer.parseInt(parts[0]);
            Long lastRankId = Long.parseLong(parts[1]);
            return new PlaceRankingLastKnownCursor(lastRank, lastRankId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 lastKnown" + lastKnown);
        }
    }

    public static PlaceRankingLastKnownCursor of(int lastRank, Long lastRankId) {
        return new PlaceRankingLastKnownCursor(lastRank, lastRankId);
    }

    public String toCursorString() {
        return lastRank + DELIMITER + lastRankId;
    }
}