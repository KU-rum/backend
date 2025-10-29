package ku_rum.backend.domain.bookmark.application;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.BOOKMARK_NOT_FOUND;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.DUPLICATE_BOOKMARK;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.UNAUTHORIZED_BOOKMARK;

import java.util.List;
import ku_rum.backend.domain.bookmark.domain.NoticeBookmark;
import ku_rum.backend.domain.bookmark.domain.repository.BookmarkRepository;
import ku_rum.backend.domain.bookmark.dto.request.CreateBookmarkRequest;
import ku_rum.backend.domain.bookmark.dto.response.CreateBookmarkResponse;
import ku_rum.backend.domain.bookmark.dto.response.GetBookmarkResponse;
import ku_rum.backend.domain.notice.application.NoticeService;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.global.GlobalException;
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
        validateDuplicateBookmark(user, notice);
        NoticeBookmark noticeBookmark = save(user, notice);
        return CreateBookmarkResponse.from(noticeBookmark);
    }


    public List<GetBookmarkResponse> getBookmark(CustomUserDetails userDetails) {
        User user = userService.getUser();
        List<NoticeBookmark> notices = bookmarkRepository.findByUser(user);

        return notices.stream()
                .map(noticeBookmark -> {
                    Notice notice = noticeBookmark.getNotice();
                    return GetBookmarkResponse.of(noticeBookmark, notice);
                })
                .toList();
    }

    @Transactional
    public void deleteBookmark(CustomUserDetails userDetails, Long bookmarkId) {
        User user = userService.getUser();
        NoticeBookmark noticeBookmark = findById(bookmarkId);
        validateBookmarkAuthorization(user, noticeBookmark);
        bookmarkRepository.deleteById(bookmarkId);
    }

    private NoticeBookmark save(User user, Notice notice) {
        NoticeBookmark noticeBookmark = NoticeBookmark.builder()
                .user(user)
                .notice(notice)
                .build();
        return bookmarkRepository.save(noticeBookmark);
    }

    private void validateDuplicateBookmark(User user, Notice notice) {
        if (bookmarkRepository.existsByUserAndNotice(user, notice)) {
            throw new GlobalException(DUPLICATE_BOOKMARK);
        }
    }

    private void validateBookmarkAuthorization(User user, NoticeBookmark noticeBookmark) {
        Long noticeId = noticeBookmark.getUser().getId();
        if (noticeId.equals(user.getId())) {
            throw new GlobalException(UNAUTHORIZED_BOOKMARK);
        }
    }

    private NoticeBookmark findById(Long bookmarkId) {
        return bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new GlobalException(BOOKMARK_NOT_FOUND));
    }
}
