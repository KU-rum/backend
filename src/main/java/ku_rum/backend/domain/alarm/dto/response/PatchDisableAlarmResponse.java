package ku_rum.backend.domain.alarm.dto.response;

import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.UserDisabledAlarm;

public record PatchDisableAlarmResponse(Long userId, AlarmType alarmType, Boolean isDisabled) {
    public static PatchDisableAlarmResponse of(UserDisabledAlarm userDisabledAlarm, boolean isDisabled) {
        return new PatchDisableAlarmResponse(userDisabledAlarm.getUser().getId(), userDisabledAlarm.getAlarmType(),
                isDisabled);
    }
}
