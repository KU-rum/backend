package ku_rum.backend.domain.alarm.application;

import java.util.Map.Entry;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.SearchKeyword;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class NewKeywordNoticeHandler implements AlarmMessageHandler {

    @Override
    public Alarm create(AlarmType alarmType, Object payload, User user) {
        Entry<SearchKeyword, Notice> entry = (Entry<SearchKeyword, Notice>) payload;
        String keyword = entry.getKey().getKeyword();
        String message = String.format("%s에 대한 공지가 올라왔어요.", keyword);

        return Alarm.builder()
                .alarmType(AlarmType.NEW_KEYWORD_NOTICE)
                .message(message)
                .isChecked(false)
                .dataId(String.valueOf(entry.getValue().getId()))
                .build();
    }

    @Override
    public Announcement create(AlarmType alarmType, Object payload) {
        return null;
    }
}
