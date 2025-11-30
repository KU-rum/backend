package ku_rum.backend.domain.alarm.domain;

import static ku_rum.backend.domain.alarm.domain.AlarmCategory.ALARM;
import static ku_rum.backend.domain.alarm.domain.AlarmCategory.ANNOUNCEMENT;

import lombok.Getter;

@Getter
public enum AlarmType {

    NEW_FRIEND_REQUEST("%s 님이 친구 신청을 했어요. 친구 신청을 수락하시겠어요?", ALARM),
    NEW_FRIEND_PLACE_SHARING("%s 님이 위치를 공유했어요. 친구 위치를 확인해보세요.", ALARM),
    NEW_NOTICE("새로운 공지가 올라왔어요. 바로 확인해보세요!", ANNOUNCEMENT),
    NEW_KEYWORD_NOTICE("%s에 대한 공지가 올라왔어요.", ALARM),
    RENEW_TOP_RANK_PLACE("%s이 가장 많이 방문한 장소가 되었어요.", ALARM),
    RENEW_RANK_PLACE("내 장소 랭킹의 순위가 바뀌었어요!", ALARM);

    private String template;
    private AlarmCategory alarmCategory;

    AlarmType(String template, AlarmCategory alarmCategory) {
        this.template = template;
        this.alarmCategory = alarmCategory;
    }
}
