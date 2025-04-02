package ku_rum.backend.domain.bookmark.application;

import ku_rum.backend.domain.bookmark.domain.Bookmark;
import ku_rum.backend.domain.bookmark.domain.repository.BookmarkRepository;
import ku_rum.backend.domain.bookmark.dto.request.BookmarkSaveRequest;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepository;
import ku_rum.backend.domain.notice.dto.response.NoticeSimpleResponse;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.exception.notice.DuplicateNoticeException;
import ku_rum.backend.global.exception.notice.NoSuchNoticeException;
import ku_rum.backend.global.exception.user.NoSuchUserException;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import ku_rum.backend.global.utill.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.DUPLICATE_NOTICE;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public void addBookmark(BookmarkSaveRequest bookmarkRequest) {
        User user = getUser();
        Notice notice = getNotice(bookmarkRequest);

        if (bookmarkRepository.existsByUserAndNotice(user, notice)) {
            throw new DuplicateNoticeException(DUPLICATE_NOTICE);
        }

        Bookmark bookmark = Bookmark.of(user, notice);
        bookmarkRepository.save(bookmark);
    }

    public List<NoticeSimpleResponse> getBookmarks() {
        User user = getUser();
        List<Bookmark> bookmarks = getBookmarksByUser(user);

        return bookmarks.stream()
                .map(bookmark -> new NoticeSimpleResponse(bookmark.getNotice()))
                .collect(Collectors.toList());
    }

    private List<Bookmark> getBookmarksByUser(User user) {
        return bookmarkRepository.findByUser(user);
    }

    private User getUser() {
        Long memberId = UserUtil.getLongMemberId();
        return userRepository.findUserById(memberId).orElseThrow(() -> new NoSuchUserException(NO_SUCH_USER));
    }

    private Notice getNotice(BookmarkSaveRequest bookmarkRequest) {
        return noticeRepository.findByUrl(bookmarkRequest.getUrl())
                .orElseThrow(() -> new NoSuchNoticeException(BaseExceptionResponseStatus.NO_SUCH_NOTICE));
    }
}
