package ku_rum.backend.domain.alarm.application;

import ku_rum.backend.domain.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestHandler implements AlarmMessageHandler {
    @Override
    public String create(Object payload) {
        User friend = (User) payload;

        return String.format("%s 님이 친구 신청을 했어요. 친구 신청을 수락하시겠어요?", friend.getNickname());
    }
}
