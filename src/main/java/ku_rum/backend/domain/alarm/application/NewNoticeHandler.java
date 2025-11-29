package ku_rum.backend.domain.alarm.application;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class NewNoticeHandler implements AlarmMessageHandler {

    @Override
    public Alarm create(AlarmType alarmType, Object object, User user) {
        return null;
    }

    @Override
    public Announcement create(AlarmType alarmType, Object object) {
        String message = String.format("새로운 공지가 올라왔어요. 바로 확인해보세요!");

        return Announcement.builder()
                .alarmType(AlarmType.NEW_NOTICE)
                .message(message)
                .isChecked(false)
                .build();
    }
}
