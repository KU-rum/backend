package ku_rum.backend.domain.notice.dto.response;

public record ViewCountResponse(
        Long count
) {
    public static ViewCountResponse of(Long count) {
        return new ViewCountResponse(count);
    }
}
