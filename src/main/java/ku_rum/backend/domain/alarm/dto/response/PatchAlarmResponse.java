package ku_rum.backend.domain.alarm.dto.response;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.UserAnnouncement;
import lombok.Builder;

@Builder
public record PatchAlarmResponse(Long id, AlarmType alarmType, String message, String dataId) {

    public static PatchAlarmResponse from(Alarm alarm) {
        return PatchAlarmResponse.builder()
                .id(alarm.getId())
                .alarmType(alarm.getAlarmType())
                .message(alarm.getMessage())
                .dataId(alarm.getDataId())
                .build();
    }

    public static PatchAlarmResponse from(UserAnnouncement userAnnouncement) {
        return PatchAlarmResponse.builder()
                .id(userAnnouncement.getId())
                .alarmType(userAnnouncement.getAnnouncement().getAlarmType())
                .message(userAnnouncement.getAnnouncement().getMessage())
                .dataId(userAnnouncement.getDataId())
                .build();
    }
}
