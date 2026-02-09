package ku_rum.backend.domain.banner.dto;

import ku_rum.backend.domain.banner.domain.Banner;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BannerResponse {
    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;

    public static BannerResponse from(Banner banner) {
        return BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .linkUrl(banner.getLinkUrl())
                .build();
    }
}
