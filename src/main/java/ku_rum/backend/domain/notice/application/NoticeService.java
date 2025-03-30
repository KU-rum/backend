package ku_rum.backend.domain.notice.application;

import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.NoticeCategory;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepository;
import ku_rum.backend.domain.notice.dto.response.NoticeSimpleResponse;
import ku_rum.backend.domain.notice.dto.response.RecentSearchTermResponse;
import ku_rum.backend.domain.notice.dto.response.ViewCountResponse;
import ku_rum.backend.global.exception.notice.InvalidPageException;
import ku_rum.backend.global.exception.notice.RedisSynchronizationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.INVALID_PAGE;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.SYNCHORNIZATION_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeService {

    private static final int PAGE_SIZE = 20;       //한 페이지에 들어갈 공지사항 개수 (추후에 논의)
    private static final String NORMAL_VIEW_COUNT_KEY = "notice:viewcount:";
    private static final String POPULAR_VIEW_COUNT_KEY = "popular:notices";

    private final NoticeRepository noticeRepository;
    private final RedisTemplate<String, String> recentSearchRedisTemplate;
    private final RedisTemplate<String,String> redisTemplate;

    /**
     * 1) 카테고리별 공지사항 조회
     */
    public List<NoticeSimpleResponse> findNoticesByCategory(NoticeCategory category, int page) {

        validatePage(page);

        List<Notice> notices = noticeRepository.findByNoticeCategoryOrderByDateDesc(category, PageRequest.of(page - 1, PAGE_SIZE));
        return notices.stream()
                .map(NoticeSimpleResponse::new)
                .toList();
    }

    /**
     * 2) 제목으로 공지사항 검색
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
     * 3) 유저 아이디로 최근 검색어 가져오기
     */
    public RecentSearchTermResponse getRecentSearchTerms(Long userId) {
        String redisKey = "user:" + userId + ":recent-searches";

        Optional<List<String>> terms = Optional.ofNullable(recentSearchRedisTemplate.opsForList().range(redisKey, 0, 9));

        return RecentSearchTermResponse.of(userId, terms.orElse(List.of()));
    }


    private static void validatePage(int page) {
        //요청으로 들어온 page는 1부터 시작해야 함
        if (page < 1) {
            throw new InvalidPageException(INVALID_PAGE);
        }
    }

    /**
     * 4) url에 대해 조회수 redis에 갱신
     */
    public ViewCountResponse increaseViewCount(String url) {

        //일반 조회수 증가
        Long currentCount = redisTemplate.opsForValue().increment(NORMAL_VIEW_COUNT_KEY+url);
        //인기공지 조회수 증가
        increasePopularViewCount(url);
        //일반 조회수에 대해 TTL 설정(2시간후 만료)
        makeTTLonViewedUrl();
        return ViewCountResponse.of(url, currentCount);
    }

    /**
     * 4-1) 인기공지 조회수 증가
     */
    private void increasePopularViewCount(String url) {
        redisTemplate.opsForZSet().incrementScore(POPULAR_VIEW_COUNT_KEY, url, 1);
    }

    /**
     * 4-2) 일반 조회수 TTL설정
     */
    private void makeTTLonViewedUrl() {
        if (redisTemplate.getExpire(NORMAL_VIEW_COUNT_KEY) == -1) {
            redisTemplate.expire(NORMAL_VIEW_COUNT_KEY, 2, TimeUnit.HOURS);
        }
    }

    /**
     * 4-3) redis의 조회수 정보 db와 동기화
     */
    @Scheduled(fixedRate = 1800000) //1800000ms = 30분
    public void syncViewCountsToDatabase() {
        log.info("[syncViewCountsToDatabase] 레디스에 저장된 정보 db에 동기화");

        //redis에서 모든 viewcount 키 조회
        Set<String> viewCountKeys = redisTemplate.keys(NORMAL_VIEW_COUNT_KEY + "*");
        if (!viewCountKeys.isEmpty()) {
            for (String key : viewCountKeys) {
                try {
                    //URL 추출(키 형식: notice:viewcount:url)
                    String url = key.substring(NORMAL_VIEW_COUNT_KEY.length());
                    //redis에서 현재 조회수 가져오기
                    String countStr = redisTemplate.opsForValue().get(key);
                    if (countStr == null) continue;

                    long count = Long.parseLong(countStr);
                    //db에 업데이트
                    noticeRepository.updateViewCount(url, count);
                    redisTemplate.delete(key);
                } catch (Exception e) {
                    throw new RedisSynchronizationException(SYNCHORNIZATION_ERROR);
                }
            }
            log.info("[syncViewCountsToDatabase] <완료> 레디스에 저장된 정보 db에 동기화 ");
        }
    }


}