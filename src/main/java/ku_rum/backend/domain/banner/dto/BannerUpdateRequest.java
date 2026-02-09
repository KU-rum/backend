package ku_rum.backend.domain.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BannerUpdateRequest {
    private String title;
    private String imageUrl;
    private String linkUrl;
    private int displayOrder;
    private boolean active;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
