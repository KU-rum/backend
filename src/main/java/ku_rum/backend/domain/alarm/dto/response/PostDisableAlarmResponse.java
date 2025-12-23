package ku_rum.backend.domain.alarm.dto.response;

import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.UserDisabledAlarm;

public record PostDisableAlarmResponse(Long userId, AlarmType alarmType, Boolean isDisabled) {
    public static PostDisableAlarmResponse of(UserDisabledAlarm userDisabledAlarm, boolean isDisabled) {
        return new PostDisableAlarmResponse(userDisabledAlarm.getUser().getId(), userDisabledAlarm.getAlarmType(),
                isDisabled);
    }
}
