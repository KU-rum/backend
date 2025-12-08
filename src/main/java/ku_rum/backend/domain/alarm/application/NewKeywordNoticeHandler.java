package ku_rum.backend.domain.alarm.application;

import static ku_rum.backend.domain.alarm.util.FcmUtil.MESSAGE_TITLE;

import java.util.List;
import java.util.Map.Entry;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.alarm.dto.FcmDirectDto;
import ku_rum.backend.domain.alarm.dto.FcmTopicDto;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.SearchKeyword;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class NewKeywordNoticeHandler implements AlarmMessageHandler {

    @Override
    public Alarm create(AlarmType alarmType, Object payload, User user) {
        Entry<SearchKeyword, Notice> entry = (Entry<SearchKeyword, Notice>) payload;
        String message = getMessage(payload);

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

    @Override
    public FcmTopicDto getFcmTopicDto(Object object) {
        return null;
    }

    @Override
    public FcmDirectDto getFcmDirectDto(Object object, User user) {

        return FcmDirectDto.builder()
                .title(MESSAGE_TITLE)
                .body(getMessage(object))
                .userIds(List.of(user.getId()))
                .build();
    }

    public String getMessage(Object object) {
        Entry<SearchKeyword, Notice> entry = (Entry<SearchKeyword, Notice>) object;
        String keyword = entry.getKey().getKeyword();
        return String.format("%s에 대한 공지가 올라왔어요.", keyword);
    }
}
