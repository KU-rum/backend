package ku_rum.backend.domain.notice.dto.response;

public record NoticeDetailResponse(
        Long id,
        String content,
        String link
) {
}
