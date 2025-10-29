package ku_rum.backend.domain.bookmark.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateBookmarkRequest(
        @NotBlank(message = "공지사항 Id은 필수 입니다.") Long noticeId
) {
}
