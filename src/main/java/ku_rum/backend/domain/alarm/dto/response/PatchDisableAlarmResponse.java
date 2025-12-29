package ku_rum.backend.domain.alarm.dto.response;

import java.util.ArrayList;
import java.util.List;
import ku_rum.backend.domain.alarm.domain.UserDisabledAlarm;

public record PatchDisableAlarmResponse(Long userId, List<DisableAlarmDto> alarms) {
    public static PatchDisableAlarmResponse of(Long userId, List<UserDisabledAlarm> toCreate,
                                               List<UserDisabledAlarm> toDelete) {

        List<DisableAlarmDto> createdAlarms = toCreate.stream()
                .map(userDisabledAlarm -> DisableAlarmDto.of(userDisabledAlarm, true))
                .toList();

        List<DisableAlarmDto> deletedAlarms = toDelete.stream()
                .map(userDisabledAlarm -> DisableAlarmDto.of(userDisabledAlarm, false))
                .toList();

        List<DisableAlarmDto> allAlarms = new ArrayList<>();
        allAlarms.addAll(createdAlarms);
        allAlarms.addAll(deletedAlarms);

        return new PatchDisableAlarmResponse(userId, allAlarms);
    }
}
