package ku_rum.backend.domain.banner.domain;

import jakarta.persistence.*;
import ku_rum.backend.domain.banner.dto.BannerUpdateRequest;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners",
        indexes = @Index(name = "idx_banner_active_order", columnList = "is_active, display_order"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
        this.active = true;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(BannerUpdateRequest req) {
        this.title = req.getTitle();
        this.imageUrl = req.getImageUrl();
        this.linkUrl = req.getLinkUrl();
        this.displayOrder = req.getDisplayOrder();
        this.startAt = req.getStartAt();
        this.endAt = req.getEndAt();
        this.active = req.isActive();
    }

    public void deactivate() {
        this.active = false;
    }

    public void changeDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
