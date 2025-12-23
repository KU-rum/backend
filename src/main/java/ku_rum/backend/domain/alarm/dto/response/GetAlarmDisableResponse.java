package ku_rum.backend.domain.alarm.dto.response;

import java.util.List;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.UserDisabledAlarm;

public record GetAlarmDisableResponse(List<AlarmType> disabledAlarmType) {
    public static GetAlarmDisableResponse from(List<UserDisabledAlarm> userDisabledAlarms) {
        return new GetAlarmDisableResponse(userDisabledAlarms.stream()
                .map(UserDisabledAlarm::getAlarmType)
                .toList());
    }
}
