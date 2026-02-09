package ku_rum.backend.domain.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerCreateRequest {
    private String title;
    private String imageUrl;
    private String linkUrl;
    private int displayOrder;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
