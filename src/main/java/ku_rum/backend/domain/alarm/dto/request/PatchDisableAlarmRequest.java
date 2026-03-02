package ku_rum.backend.domain.alarm.dto.request;

import java.util.List;
import ku_rum.backend.domain.alarm.domain.AlarmType;

public record PatchDisableAlarmRequest(List<AlarmType> alarmTypes) {
}
