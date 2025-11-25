package ku_rum.backend.domain.alarm.application;

import org.springframework.stereotype.Component;

@Component
public class RenewRankPlaceHandler implements AlarmMessageHandler {
    @Override
    public String create(Object payload) {
        return "";
    }
}
