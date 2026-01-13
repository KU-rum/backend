package ku_rum.backend.domain.notice.application;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_NOTICE;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_NOTICE_DETAIL;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.bookmark.domain.NoticeBookmark;
import ku_rum.backend.domain.bookmark.domain.repository.BookmarkRepository;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.NoticeDetail;
import ku_rum.backend.domain.notice.domain.PublishStatus;
import ku_rum.backend.domain.notice.domain.repository.NoticeDetailRepository;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepository;
import ku_rum.backend.domain.notice.dto.response.NoticeDetailResponse;
import ku_rum.backend.domain.notice.dto.response.NoticeResponse;
import ku_rum.backend.domain.search.application.RecentSearchService;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.global.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private static final int POPULAR_NOTICE_COUNT = 3;
    private static final int PRIMARY_NOTICE_COUNT = 3;

    private final NoticeRepository noticeRepository;
    private final NoticeDetailRepository noticeDetailRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;
    private final RecentSearchService recentSearchService;

    public Page<NoticeResponse> findByCategory(Long categoryId, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "pub_date")
        );
        return noticeRepository.findByCategoryIdAndPublishStatus(categoryId, PublishStatus.SUCCESS_CRAWLING,
                sortedPageable).map(NoticeResponse::from);
    }

    public NoticeDetailResponse findByNoticeId(Long noticeId) {
        User user = userService.getUser();
        Notice notice = findNoticeByNoticeId(noticeId);
        NoticeDetail noticeDetail = noticeDetailRepository.findByNotice(notice)
                .orElseThrow(() -> new GlobalException(NO_SUCH_NOTICE_DETAIL));
        String encodedHtml = noticeDetail.getHtmlContent();
        Optional<NoticeBookmark> noticeBookmark = bookmarkRepository.findByUserAndNotice(user, notice);
        if (noticeBookmark.isPresent()) {
            return new NoticeDetailResponse(noticeDetail.getNotice().getId(), encodedHtml, notice.getLink(),
                    noticeDetail.getNotice().getTitle(), noticeDetail.getNotice().getPubDate(),
                    noticeBookmark.get().getId(), true);
        }
        return new NoticeDetailResponse(noticeDetail.getNotice().getId(), encodedHtml, notice.getLink(),
                noticeDetail.getNotice().getTitle(), noticeDetail.getNotice().getPubDate(), -1L, false);
    }

    public Notice findNoticeByNoticeId(Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> new GlobalException(NO_SUCH_NOTICE));
    }

    public List<NoticeResponse> findPopularNotice() {
        Long limit = Long.valueOf(POPULAR_NOTICE_COUNT);
        return noticeRepository.findTopByBookmark(limit)
                .stream()
                .map(NoticeResponse::from)
                .toList();
    }


    public List<NoticeResponse> findPrimaryNotices() {
        Pageable pageable = PageRequest.of(
                0,
                PRIMARY_NOTICE_COUNT,
                Sort.by(Sort.Direction.DESC, "id") // 가장 최근 공지 우선
        );

        return noticeRepository
                .findByIsImportantTrueAndPublishStatus(PublishStatus.SUCCESS_CRAWLING, pageable)
                .stream()
                .map(NoticeResponse::from)
                .toList();
    }

    // 키워드 검색 로직
    public Page<NoticeResponse> searchByKeyword(String keyword, Pageable pageable) {
        recentSearchService.save(keyword);

        return noticeRepository.searchByKeyword(keyword, PublishStatus.SUCCESS_CRAWLING, pageable)
                .map(NoticeResponse::from);
    }
}

