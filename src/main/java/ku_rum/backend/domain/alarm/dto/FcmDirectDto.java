package ku_rum.backend.domain.alarm.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record FcmDirectDto(String title, String body, List<Long> userIds) {
}
