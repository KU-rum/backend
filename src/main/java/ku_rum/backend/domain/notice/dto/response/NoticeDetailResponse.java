package ku_rum.backend.domain.notice.dto.response;

import java.time.LocalDateTime;

public record NoticeDetailResponse(
        Long id,
        String content,
        String link,
        String title,
        LocalDateTime pubdate
) {
}
