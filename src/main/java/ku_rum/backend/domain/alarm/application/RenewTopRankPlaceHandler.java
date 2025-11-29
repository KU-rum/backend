package ku_rum.backend.domain.alarm.application;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.place.application.RankingChangeDto;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class RenewTopRankPlaceHandler implements AlarmMessageHandler {

    @Override
    public Alarm create(AlarmType alarmType, Object payload, User user) {
        RankingChangeDto rankingChangeDto = (RankingChangeDto) payload;
        String name = rankingChangeDto.placeRank().getPlace().getName();
        String message = String.format("%s이 가장 많이 방문한 장소가 되었어요.", name);

        return Alarm.builder()
                .alarmType(AlarmType.RENEW_TOP_RANK_PLACE)
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
