package ku_rum.backend.domain.alarm.dto.response;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import lombok.Builder;

@Builder
public record GetAlarmDto(Long id, AlarmType alarmType, String message, String dataId) {

    public static GetAlarmDto from(Alarm alarm) {
        return GetAlarmDto.builder()
                .id(alarm.getId())
                .alarmType(alarm.getAlarmType())
                .message(alarm.getMessage())
                .dataId(alarm.getDataId())
                .build();
    }
}
