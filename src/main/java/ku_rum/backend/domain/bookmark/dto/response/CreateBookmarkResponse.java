package ku_rum.backend.domain.bookmark.dto.response;

import ku_rum.backend.domain.bookmark.domain.NoticeBookmark;

public record CreateBookmarkResponse(Long bookmarkId) {

    public static CreateBookmarkResponse from(NoticeBookmark noticeBookmark) {
        return new CreateBookmarkResponse(noticeBookmark.getId());
    }
}
