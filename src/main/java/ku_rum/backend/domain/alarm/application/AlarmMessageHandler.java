package ku_rum.backend.domain.alarm.application;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.user.domain.User;

public interface AlarmMessageHandler {
    Alarm create(AlarmType alarmType, Object object, User user);

    Announcement create(AlarmType alarmType, Object object);
}
