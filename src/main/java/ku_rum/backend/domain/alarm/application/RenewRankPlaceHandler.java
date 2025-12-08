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
public class RenewRankPlaceHandler implements AlarmMessageHandler {
    @Override
    public Alarm create(AlarmType alarmType, Object object, User user) {
        return Alarm.builder()
                .alarmType(AlarmType.RENEW_RANK_PLACE)
                .message(getMessage())
                .isChecked(false)
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
                .body(getMessage())
                .userIds(List.of(user.getId()))
                .build();
    }

    public String getMessage() {
        return "내 장소 랭킹의 순위가 바뀌었어요!";
    }
}
