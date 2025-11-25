package ku_rum.backend.domain.alarm.application;

import org.springframework.stereotype.Component;

@Component
public class RenewRankPlaceHandler implements AlarmMessageHandler {
    @Override
    public String create(Object payload) {
        return String.format("내 장소 랭킹의 순위가 바뀌었어요!");
    }
}
