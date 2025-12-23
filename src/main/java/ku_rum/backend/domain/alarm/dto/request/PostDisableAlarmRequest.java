package ku_rum.backend.domain.alarm.dto.request;

import ku_rum.backend.domain.alarm.domain.AlarmType;

public record PostDisableAlarmRequest(AlarmType alarmType) {
}
