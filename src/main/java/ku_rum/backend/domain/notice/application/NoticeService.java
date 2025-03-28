package ku_rum.backend.domain.notice.application;

import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.NoticeCategory;
import ku_rum.backend.domain.notice.domain.NoticeStatus;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepository;
import ku_rum.backend.domain.notice.dto.response.NoticeSimpleResponse;
import ku_rum.backend.domain.notice.dto.response.RecentSearchTerm;
import ku_rum.backend.global.exception.notice.InvalidPageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.INVALID_PAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeService {

    private static final int PAGE_SIZE = 20;       //한 페이지에 들어갈 공지사항 개수 (추후에 논의)

    private final NoticeRepository noticeRepository;
    private final RedisTemplate<String, String> recentSearchRedisTemplate;

    /**
     * 카테고리별 공지사항 조회
     */
    public List<NoticeSimpleResponse> findNoticesByCategory(NoticeCategory category, int page) {

        validatePage(page);

        List<Notice> notices = noticeRepository.findByNoticeCategoryOrderByDateDesc(category, PageRequest.of(page - 1, PAGE_SIZE));
        return notices.stream()
                .map(NoticeSimpleResponse::new)
                .toList();
    }

    /**
     * 제목으로 공지사항 검색
     */
    public List<NoticeSimpleResponse> searchNoticesByTitle(Long userId, String searchTerm, int page) {
        // 최근 검색어는 최대 10개만 유지
        String redisKey = "user:" + userId + ":recent-searches";
        recentSearchRedisTemplate.opsForList().leftPush(redisKey, searchTerm.trim());
        recentSearchRedisTemplate.opsForList().trim(redisKey, 0, 9);

        validatePage(page);

        List<Notice> notices = noticeRepository.searchNoticesByTitleWithPaging(searchTerm.trim(), page - 1, PAGE_SIZE);

        return notices.stream()
                .map(notice -> NoticeSimpleResponse.builder()
                        .url(notice.getUrl())
                        .title(notice.getTitle())
                        .date(notice.getDate())
                        .category(notice.getNoticeCategory().getText())
                        .isImportant(notice.getNoticeStatus().isImportant())
                        .build())
                .toList();
    }



    /**
     * 유저 아이디로 최근 검색어 가져오기
     */
    public RecentSearchTerm getRecentSearchTerms(Long userId) {
        String redisKey = "user:" + userId + ":recent-searches";

        Optional<List<String>> terms = Optional.ofNullable(recentSearchRedisTemplate.opsForList().range(redisKey, 0, 9));

        return RecentSearchTerm.of(userId, terms.orElse(List.of()));
    }


    private static void validatePage(int page) {
        //요청으로 들어온 page는 1부터 시작해야 함
        if (page < 1) {
            throw new InvalidPageException(INVALID_PAGE);
        }
    }

}
