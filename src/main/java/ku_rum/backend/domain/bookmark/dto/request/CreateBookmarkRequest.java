package ku_rum.backend.domain.bookmark.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateBookmarkRequest(
        @NotNull(message = "공지사항 Id은 필수 입니다.") Long noticeId
) {
}
