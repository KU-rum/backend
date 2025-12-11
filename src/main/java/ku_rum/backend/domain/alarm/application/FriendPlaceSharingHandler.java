package ku_rum.backend.domain.alarm.application;

import static ku_rum.backend.domain.alarm.util.FcmUtil.MESSAGE_TITLE;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.UNSUPPORTED_TOPIC_FCM;

import java.util.List;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.alarm.dto.FcmDirectDto;
import ku_rum.backend.domain.alarm.dto.FcmTopicDto;
import ku_rum.backend.domain.place.dto.UserPlaceAlarmDto;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.global.GlobalException;
import org.springframework.stereotype.Component;

@Component
public class FriendPlaceSharingHandler implements AlarmMessageHandler {
    @Override
    public Alarm create(AlarmType alarmType, Object object, User user) {
        UserPlaceAlarmDto userPlaceAlarmDto = (UserPlaceAlarmDto) object;
        return Alarm.builder()
                .alarmType(alarmType)
                .message(getMessage(object))
                .isChecked(false)
                .dataId(String.valueOf(userPlaceAlarmDto.user()))
                .user(user)
                .build();
    }

    @Override
    public Announcement create(AlarmType alarmType, Object object) {
        return null;
    }

    @Override
    public FcmTopicDto getFcmTopicDto(Object object) {
        throw new GlobalException(UNSUPPORTED_TOPIC_FCM);
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
        UserPlaceAlarmDto userPlaceAlarmDto = (UserPlaceAlarmDto) object;
        return String.format("%s 님이 위치를 공유했어요. 친구 위치를 확인해보세요.", userPlaceAlarmDto.user().getNickname());
    }
}
