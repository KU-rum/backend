package ku_rum.backend.domain.bookmark.application;

import ku_rum.backend.domain.bookmark.domain.NoticeBookmark;
import ku_rum.backend.domain.bookmark.domain.repository.BookmarkRepository;
import ku_rum.backend.domain.bookmark.dto.request.CreateBookmarkRequest;
import ku_rum.backend.domain.bookmark.dto.response.CreateBookmarkResponse;
import ku_rum.backend.domain.notice.application.NoticeService;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;
    private final NoticeService noticeService;

    @Transactional
    public CreateBookmarkResponse createBookmark(CustomUserDetails userDetails, CreateBookmarkRequest request) {
        User user = userService.getUser();
        Notice notice = noticeService.findNoticeByNoticeId(request.noticeId());
        NoticeBookmark noticeBookmark = save(user, notice);
        return CreateBookmarkResponse.from(noticeBookmark);
    }

    private NoticeBookmark save(User user, Notice notice) {
        NoticeBookmark noticeBookmark = NoticeBookmark.builder()
                .user(user)
                .notice(notice)
                .build();
        return bookmarkRepository.save(noticeBookmark);
    }
}
