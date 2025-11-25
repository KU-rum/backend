package ku_rum.backend.domain.alarm.application;

import org.springframework.stereotype.Component;

@Component
public class NewNoticeHandler implements AlarmMessageHandler {
    @Override
    public String create(Object payload) {
        return String.format("새로운 공지가 올라왔어요. 바로 확인해보세요!");
    }
}
