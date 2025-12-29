package ku_rum.backend.domain.alarm.dto.response;

import java.util.ArrayList;
import java.util.List;
import ku_rum.backend.domain.alarm.domain.UserDisabledAlarm;

public record PatchDisableAlarmResponse(Long userId, List<DisableAlarmDto> alarms) {
    public static PatchDisableAlarmResponse of(List<UserDisabledAlarm> toCreate, List<UserDisabledAlarm> toDelete) {
        Long userId = null;
        if (!toCreate.isEmpty()) {
            userId = toCreate.get(0).getUser().getId();
        } else if (!toDelete.isEmpty()) {
            userId = toDelete.get(0).getUser().getId();
        }

        List<DisableAlarmDto> createdAlarms = toCreate.stream()
                .map(userDisabledAlarm -> DisableAlarmDto.of(userDisabledAlarm, false))
                .toList();

        List<DisableAlarmDto> deletedAlarms = toDelete.stream()
                .map(userDisabledAlarm -> DisableAlarmDto.of(userDisabledAlarm, true))
                .toList();

        List<DisableAlarmDto> allAlarms = new ArrayList<>();
        allAlarms.addAll(createdAlarms);
        allAlarms.addAll(deletedAlarms);

        return new PatchDisableAlarmResponse(userId, allAlarms);
    }
}
