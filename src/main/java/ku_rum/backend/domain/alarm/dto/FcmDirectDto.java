package ku_rum.backend.domain.alarm.dto;

import java.util.List;

public record FcmDirectDto(String title, String body, List<Long> userIds) {
}
