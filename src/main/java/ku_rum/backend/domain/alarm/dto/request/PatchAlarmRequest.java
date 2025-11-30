package ku_rum.backend.domain.alarm.dto.request;

import ku_rum.backend.domain.alarm.domain.AlarmCategory;

public record PatchAlarmRequest(Long alarmId, AlarmCategory alarmCategory) {
}
