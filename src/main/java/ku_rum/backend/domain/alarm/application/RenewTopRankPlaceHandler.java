package ku_rum.backend.domain.alarm.application;

import ku_rum.backend.domain.place.application.RankingChangeDto;
import org.springframework.stereotype.Component;

@Component
public class RenewTopRankPlaceHandler implements AlarmMessageHandler {
    @Override
    public String create(Object payload) {
        RankingChangeDto rankingChangeDto = (RankingChangeDto) payload;
        String name = rankingChangeDto.placeRank().getPlace().getName();
        return String.format("%s이 가장 많이 방문한 장소가 되었어요.", name);
    }
}
