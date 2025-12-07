package ku_rum.backend.domain.alarm.application;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.place.dto.UserPlaceAlarmDto;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class FriendPlaceSharingHandler implements AlarmMessageHandler {
    @Override
    public Alarm create(AlarmType alarmType, Object object, User user) {
        UserPlaceAlarmDto userPlaceAlarmDto = (UserPlaceAlarmDto) object;
        String message = String.format("%s 님이 위치를 공유했어요. 친구 위치를 확인해보세요.", userPlaceAlarmDto.user().getNickname());
        return Alarm.builder()
                .alarmType(alarmType)
                .message(message)
                .isChecked(false)
                .dataId(String.valueOf(userPlaceAlarmDto.user()))
                .user(user)
                .build();
    }

    @Override
    public Announcement create(AlarmType alarmType, Object object) {
        return null;
    }
}
