package ku_rum.backend.domain.alarm.dto.request;

import java.util.List;

public record DirectNotificationRequest(List<Long> userIds, String title, String body) {
}
