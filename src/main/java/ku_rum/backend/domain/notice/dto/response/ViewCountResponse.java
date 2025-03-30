package ku_rum.backend.domain.notice.dto.response;

public record ViewCountResponse(
        String url,
        Long count
) {
    public static ViewCountResponse of(String url, Long count) {
        return new ViewCountResponse(url, count);
    }
}
