package ku_rum.backend.domain.alarm.dto.response;

import lombok.Builder;

@Builder
public record GetAlarmUnreadResponse(boolean hasUnread, int count) {
    public static GetAlarmUnreadResponse of(long unCheckedAlarm, long unCheckAnnouncementCount) {
        return GetAlarmUnreadResponse.builder()
                .hasUnread(unCheckedAlarm + unCheckAnnouncementCount > 0)
                .count((int) (unCheckedAlarm + unCheckAnnouncementCount))
                .build();
    }
}
