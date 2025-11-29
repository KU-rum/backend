package ku_rum.backend.domain.alarm.application;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class RenewRankPlaceHandler implements AlarmMessageHandler {
    @Override
    public Alarm create(AlarmType alarmType, Object object, User user) {
        String message = String.format("내 장소 랭킹의 순위가 바뀌었어요!");

        return Alarm.builder()
                .alarmType(AlarmType.RENEW_RANK_PLACE)
                .message(message)
                .isChecked(false)
                .user(user)
                .build();
    }

    @Override
    public Announcement create(AlarmType alarmType, Object object) {
        return null;
    }
}
