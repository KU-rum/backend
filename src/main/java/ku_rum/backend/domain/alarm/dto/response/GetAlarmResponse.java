package ku_rum.backend.domain.alarm.dto.response;

import java.util.List;

public record GetAlarmResponse(List<GetAlarmDto> alarms, boolean hasNext, String nextCursor) {
}
