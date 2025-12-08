package ku_rum.backend.domain.alarm.application;

import static ku_rum.backend.domain.alarm.util.FcmUtil.MESSAGE_TITLE;

import java.util.List;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.alarm.dto.FcmDirectDto;
import ku_rum.backend.domain.alarm.dto.FcmTopicDto;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestHandler implements AlarmMessageHandler {
    @Override
    public Alarm create(AlarmType alarmType, Object object, User user) {
        User friend = (User) object;

        return Alarm.builder()
                .alarmType(alarmType)
                .message(getMessage(object))
                .isChecked(false)
                .dataId(String.valueOf(friend.getNickname()))
                .user(user)
                .build();
    }

    @Override
    public Announcement create(AlarmType alarmType, Object object) {
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
        User friend = (User) object;
        return String.format("%s 님이 친구 신청을 했어요. 친구 신청을 수락하시겠어요?", friend.getNickname());
    }
}
