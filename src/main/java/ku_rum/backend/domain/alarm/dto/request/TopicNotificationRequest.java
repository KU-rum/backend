package ku_rum.backend.domain.alarm.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TopicNotificationRequest(@NotBlank(message = "토픽은 필수입니다") String topic,
                                       @NotBlank(message = "제목은 필수입니다") String title,
                                       @NotBlank(message = "내용은 필수입니다") String body) {
}
