package ku_rum.backend.domain.notice.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import ku_rum.backend.domain.alarm.application.AlarmService;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.PublishStatus;
import ku_rum.backend.domain.notice.domain.SearchKeyword;
import ku_rum.backend.domain.notice.domain.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeAlarmService {

    private final static int PAST_HOUR = 24;

    private final NoticeRepository noticeRepository;
    private final AlarmService alarmService;
    private final SearchKeywordService searchKeywordService;

    @Scheduled(cron = "0 0 12 * * ?")
    public void checkNewAlarm() {
        LocalDateTime sinceTime = LocalDateTime.now().minusHours(PAST_HOUR);
        List<Notice> notice = noticeRepository.findByPublishStatusAndPubDateAfter(
                PublishStatus.SUCCESS_CRAWLING, sinceTime);
        log.info("생성된 알림 조회 {}", notice.size());
        if (notice.isEmpty()) {
            return;
        }
        checkNewKeywordAlarm(notice);
        alarmService.notifyAlarm(AlarmType.NEW_NOTICE, notice.get(0));
    }

    private void checkNewKeywordAlarm(List<Notice> notices) {
        Map<SearchKeyword, Notice> noticeWithKeyword = searchKeywordService.findNoticeWithKeyword(notices);
        noticeWithKeyword.entrySet().stream()
                .forEach(entry -> alarmService.notifyAlarm(AlarmType.NEW_KEYWORD_NOTICE, entry));
    }
}
