package ku_rum.backend.domain.notice.application;

import java.time.LocalDateTime;
import java.util.List;
import ku_rum.backend.domain.alarm.application.AlarmService;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.PublishStatus;
import ku_rum.backend.domain.notice.domain.repository.NoticeDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeAlarmService {

    private final static int PAST_HOUR = 24;

    private final NoticeDetailRepository noticeDetailRepository;
    private final AlarmService alarmService;

    @Scheduled
    public void checkNewAlarm() {
        LocalDateTime sinceTime = LocalDateTime.now().minusHours(PAST_HOUR);
        List<Notice> notice = noticeDetailRepository.findByPublishStatusAndPubDateAfter(
                PublishStatus.SUCCESS_CRAWLING, sinceTime);
        alarmService.notifyAlarm(AlarmType.NEW_NOTICE, new Object());
    }
}
