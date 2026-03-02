package ku_rum.backend.domain.alarm.dto;

import lombok.Builder;

@Builder
public record FcmTopicDto(String title, String body, String topic) {
}
