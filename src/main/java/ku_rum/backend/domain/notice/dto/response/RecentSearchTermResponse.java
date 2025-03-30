package ku_rum.backend.domain.notice.dto.response;

import java.util.List;

public record RecentSearchTermResponse(
        Long userId,
        List<String> searchedList
) {

    public static RecentSearchTermResponse of(Long userId, List<String> searchedList) {
        return new RecentSearchTermResponse(userId, searchedList);
    }
}
