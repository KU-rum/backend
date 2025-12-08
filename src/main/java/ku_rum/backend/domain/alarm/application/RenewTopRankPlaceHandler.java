package ku_rum.backend.domain.alarm.application;

import static ku_rum.backend.domain.alarm.util.FcmUtil.MESSAGE_TITLE;

import java.util.List;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.alarm.dto.FcmDirectDto;
import ku_rum.backend.domain.alarm.dto.FcmTopicDto;
import ku_rum.backend.domain.place.application.RankingChangeDto;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class RenewTopRankPlaceHandler implements AlarmMessageHandler {

    @Override
    public Alarm create(AlarmType alarmType, Object payload, User user) {
        return Alarm.builder()
                .alarmType(AlarmType.RENEW_TOP_RANK_PLACE)
                .message(getMessage(payload))
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
                .body(getMessage(object))
                .userIds(List.of(user.getId()))
                .build();
    }

    public String getMessage(Object payload) {
        RankingChangeDto rankingChangeDto = (RankingChangeDto) payload;
        String name = rankingChangeDto.placeRank().getPlace().getName();
        return String.format("%s이 가장 많이 방문한 장소가 되었어요.", name);
    }
}
