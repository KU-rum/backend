package ku_rum.backend.domain.alarm.application;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.alarm.dto.FcmDirectDto;
import ku_rum.backend.domain.alarm.dto.FcmTopicDto;
import ku_rum.backend.domain.user.domain.User;

public interface AlarmMessageHandler {
    Alarm create(AlarmType alarmType, Object object, User user);

    Announcement create(AlarmType alarmType, Object object);

    FcmTopicDto getFcmTopicDto(Object object);

    FcmDirectDto getFcmDirectDto(Object object, User user);
}
