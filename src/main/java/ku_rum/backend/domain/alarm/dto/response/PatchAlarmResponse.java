package ku_rum.backend.domain.alarm.dto.response;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
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
}
