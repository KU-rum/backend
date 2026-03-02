package ku_rum.backend.domain.alarm.config;

import java.util.HashMap;
import java.util.Map;
import ku_rum.backend.domain.alarm.application.AlarmMessageHandler;
import ku_rum.backend.domain.alarm.application.FriendPlaceSharingHandler;
import ku_rum.backend.domain.alarm.application.FriendRequestHandler;
import ku_rum.backend.domain.alarm.application.NewKeywordNoticeHandler;
import ku_rum.backend.domain.alarm.application.NewNoticeHandler;
import ku_rum.backend.domain.alarm.application.RenewRankPlaceHandler;
import ku_rum.backend.domain.alarm.application.RenewTopRankPlaceHandler;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlarmMessageHandlerConfig {

    @Bean
    public Map<AlarmType, AlarmMessageHandler> alarmMessageHandlers() {
        Map<AlarmType, AlarmMessageHandler> alarmMessageHandlers = new HashMap<>();
        alarmMessageHandlers.put(AlarmType.NEW_FRIEND_REQUEST, new FriendRequestHandler());
        alarmMessageHandlers.put(AlarmType.NEW_FRIEND_PLACE_SHARING, new FriendPlaceSharingHandler());
        alarmMessageHandlers.put(AlarmType.NEW_NOTICE, new NewNoticeHandler());
        alarmMessageHandlers.put(AlarmType.NEW_KEYWORD_NOTICE, new NewKeywordNoticeHandler());
        alarmMessageHandlers.put(AlarmType.RENEW_TOP_RANK_PLACE, new RenewTopRankPlaceHandler());
        alarmMessageHandlers.put(AlarmType.RENEW_RANK_PLACE, new RenewRankPlaceHandler());
        return alarmMessageHandlers;
    }
}
