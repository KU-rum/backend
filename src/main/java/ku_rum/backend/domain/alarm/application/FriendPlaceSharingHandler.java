package ku_rum.backend.domain.alarm.application;

import ku_rum.backend.domain.place.dto.UserPlaceAlarmDto;
import org.springframework.stereotype.Component;

@Component
public class FriendPlaceSharingHandler implements AlarmMessageHandler {
    @Override
    public String create(Object payload) {
        UserPlaceAlarmDto userPlaceAlarmDto = (UserPlaceAlarmDto) payload;
        return String.format("%s 님이 위치를 공유했어요. 친구 위치를 확인해보세요.", userPlaceAlarmDto.user());
    }
}
