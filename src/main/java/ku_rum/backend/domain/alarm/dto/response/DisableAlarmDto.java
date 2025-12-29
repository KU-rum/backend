package ku_rum.backend.domain.alarm.dto.response;

import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.UserDisabledAlarm;

public record DisableAlarmDto(AlarmType alarmType, Boolean isDisabled) {
    public static DisableAlarmDto of(UserDisabledAlarm userDisabledAlarm, boolean isDisabled) {
        return new DisableAlarmDto(userDisabledAlarm.getAlarmType(), isDisabled);
    }
}
