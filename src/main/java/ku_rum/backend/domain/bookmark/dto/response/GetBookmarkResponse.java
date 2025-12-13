package ku_rum.backend.domain.bookmark.dto.response;

import ku_rum.backend.domain.bookmark.domain.NoticeBookmark;
import ku_rum.backend.domain.notice.domain.Notice;

import java.time.LocalDateTime;

public record GetBookmarkResponse(
        Long bookmarkId, Long noticeId, String noticeName, LocalDateTime noticePubDate, LocalDateTime bookmarkDate
) {

    public static GetBookmarkResponse of(NoticeBookmark noticeBookmark, Notice notice) {
        return new GetBookmarkResponse(noticeBookmark.getId(), notice.getId(), notice.getTitle(), notice.getPubDate(), noticeBookmark.getCreatedAt());
    }
}
