package ku_rum.backend.domain.alarm.application;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestHandler implements AlarmMessageHandler {
    @Override
    public Alarm create(AlarmType alarmType, Object object, User user) {
        User friend = (User) object;
        String message = String.format("%s 님이 친구 신청을 했어요. 친구 신청을 수락하시겠어요?", friend.getNickname());

        return Alarm.builder()
                .alarmType(alarmType)
                .message(message)
                .isChecked(false)
                .dataId(String.valueOf(friend.getNickname()))
                .user(user)
                .build();
    }

    @Override
    public Announcement create(AlarmType alarmType, Object object) {
        return null;
    }
}
