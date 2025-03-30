package ku_rum.backend.domain.notice.application;

import ku_rum.backend.domain.notice.domain.repository.NoticeRepository;
import ku_rum.backend.global.exception.notice.RedisSynchronizationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.SYNCHORNIZATION_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableAsync
public class ViewCountService {
    private static final String NORMAL_VIEW_COUNT_KEY = "notice:viewcount:";
    private static final String POPULAR_VIEW_COUNT_KEY = "popular:notices";
    private static final String LOCK_QUEUE_KEY = "lock:viewcount:queue";


    private final RedissonClient redissonClient;
    private final NoticeRepository noticeRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public void enqueueLockRequest(String url) {
        redisTemplate.opsForList().leftPush(LOCK_QUEUE_KEY, url);
    }

    /**
     * url에 대해 조회수 redis에 갱신
     */
    @Scheduled(fixedRate = 60000) //300초마다 큐 확인
    public void processQueue() {
        String url = redisTemplate.opsForList().rightPop(LOCK_QUEUE_KEY);
        if (url != null) {
            handleLockRequest(url);
        }
    }

    /**
     * 큐에서 꺼낸 작업을 처리하는 메소드
     */
    private void handleLockRequest(String url) {
        String lockKey = NORMAL_VIEW_COUNT_KEY + url;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(3, 1, TimeUnit.SECONDS)) {
                try {
                    //일반 조회수 증가
                    redisTemplate.opsForValue().increment(lockKey);
                    //인기공지 조회수 증가 - 1)
                    increasePopularViewCount(url);
                    //TTL 설정 - 2)
                    makeTTLonViewedUrl(lockKey);

                } finally {
                    lock.unlock();
                }
            } else {
                //락을 획득하지 못했을 경우 큐에 재시도할 수 있도록 넣기
                enqueueLockRequest(url);
            }
        } catch (InterruptedException e) {
            throw new RedisSynchronizationException(SYNCHORNIZATION_ERROR);        }
    }


    /**
     * 1) 인기공지 조회수 증가
     */
    private void increasePopularViewCount(String url) {
        redisTemplate.opsForZSet().incrementScore(POPULAR_VIEW_COUNT_KEY, url, 1);
    }

    /**
     * 2) 일반 조회수 TTL 설정
     */
    private void makeTTLonViewedUrl(String lockKey) {
        //TTL 설정 전에 기존 값이 존재하는지 확인
        if (redisTemplate.getExpire(lockKey) == -1) {
            redisTemplate.expire(lockKey, 2, TimeUnit.HOURS); // 2시간 TTL 설정
        }
    }

    /**
     * redis의 조회수 정보 db와 동기화
     */
    @Scheduled(fixedRate = 1800000) // 30분마다 실행
    public void syncViewCountsToDatabase() {
        log.info("[syncViewCountsToDatabase] 레디스에 저장된 정보 db에 동기화");

        //redis에서 모든 viewcount 키 조회
        Set<String> viewCountKeys = redisTemplate.keys(NORMAL_VIEW_COUNT_KEY + "*");
        if (!viewCountKeys.isEmpty()) {
            for (String key : viewCountKeys) {
                try {
                    //URL 추출 (키 형식: notice:viewcount:url)
                    String url = key.substring(NORMAL_VIEW_COUNT_KEY.length());
                    //redis에서 현재 조회수 가져오기
                    String countStr = redisTemplate.opsForValue().get(key);
                    if (countStr == null) continue;

                    long count = Long.parseLong(countStr);
                    //db에 업데이트
                    noticeRepository.updateViewCount(url, count);
                    //동기화 후 redis에서 해당 키 삭제
                    redisTemplate.delete(key);
                } catch (Exception e) {
                    throw new RedisSynchronizationException(SYNCHORNIZATION_ERROR);
                }
            }
            log.info("[syncViewCountsToDatabase] <완료> 레디스에 저장된 정보 db에 동기화 ");
        } else {
            log.info("[syncViewCountsToDatabase] <미실행> 동기화할 레디스 키가 없습니다.");
        }
    }

    /**
     * 인기 공지 top 3개 반환
     * @return
     */
    public List<String> mostViewedNotices() {
        Set<String> topNotices = redisTemplate.opsForZSet()
                .reverseRange(POPULAR_VIEW_COUNT_KEY, 0, 2); //상위 3개

        return topNotices.stream()
                .map(noticeRepository::findTitleByUrl)
                .filter(title -> title != null)
                .collect(Collectors.toList());
    }


}
