package ku_rum.backend.domain.bookmark.application;

import ku_rum.backend.domain.bookmark.domain.Bookmark;
import ku_rum.backend.domain.bookmark.domain.repository.BookmarkRepository;
import ku_rum.backend.domain.bookmark.dto.request.BookmarkSaveRequest;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.NoticeCategory;
import ku_rum.backend.domain.notice.domain.NoticeStatus;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepository;
import ku_rum.backend.domain.notice.dto.response.NoticeSimpleResponse;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.AgreementStatus;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.utill.UserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
class BookmarkServiceTest {

    @InjectMocks
    private BookmarkService bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NoticeRepository noticeRepository;

    private MockedStatic<UserUtil> mockedStatic;

    private User user;

    private Notice notice;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock 데이터 생성
        user = User.of("123@naver.com", "kmw106933@konkuk.ac.kr","홍길동", "password", "20202020", null, AgreementStatus.AGREED);
        notice = Notice.builder()
                .title("Notice")
                .url("https://konkuk.ac.kr")
                .date("2024-11-08")
                .noticeStatus(NoticeStatus.GENERAL)
                .noticeCategory(NoticeCategory.AFFAIR)
                .build();

        mockedStatic = mockStatic(UserUtil.class);
        mockedStatic.when(UserUtil::getLongMemberId).thenReturn(1L);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @DisplayName("북마크 추가 성공")
    @Test
    @WithMockUser
    void addBookmarkSuccess() {
        // given

        BookmarkSaveRequest request = new BookmarkSaveRequest(notice.getUrl());
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        when(noticeRepository.findByUrl(notice.getUrl())).thenReturn(Optional.of(notice));
        when(bookmarkRepository.findByUserAndNotice(user, notice)).thenReturn(Optional.empty());
        // when
        bookmarkService.addBookmark(request);

        // then
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    @DisplayName("북마크 조회 성공")
    @Test
    @WithMockUser
    void getBookmarksSuccess() {
        // given
        Bookmark bookmark = Bookmark.of(user, notice);

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        when(bookmarkRepository.findByUser(user)).thenReturn(List.of(bookmark));

        // when
        List<NoticeSimpleResponse> bookmarks = bookmarkService.getBookmarks();

        // then
        assertEquals(1, bookmarks.size());
        assertEquals("Notice", bookmarks.get(0).getTitle());
        assertEquals("학사", bookmarks.get(0).getCategory());
    }

}
