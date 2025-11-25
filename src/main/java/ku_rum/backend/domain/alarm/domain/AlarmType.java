package ku_rum.backend.domain.alarm.domain;

import lombok.Getter;

@Getter
public enum AlarmType {

    NEW_FRIEND_REQUEST("%s 님이 친구 신청을 했어요. 친구 신청을 수락하시겠어요?", Alarm.class),
    NEW_FRIEND_PLACE_SHARING("%s 님이 위치를 공유했어요. 친구 위치를 확인해보세요.", Alarm.class),
    NEW_NOTICE("새로운 공지가 올라왔어요. 바로 확인해보세요!", Announcement.class),
    NEW_KEYWORD_NOTICE("%s에 대한 공지가 올라왔어요.", Alarm.class),
    RENEW_TOP_RANK_PLACE("%s이 가장 많이 방문한 장소가 되었어요.", Alarm.class),
    RENEW_RANK_PLACE("내 장소 랭킹의 순위가 바뀌었어요!", Alarm.class);

    private String template;
    private Class<?> classType;

    AlarmType(String template, Class<?> classType) {
        this.template = template;
        this.classType = classType;
    }
}
