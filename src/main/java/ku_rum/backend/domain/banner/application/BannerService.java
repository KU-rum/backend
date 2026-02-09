package ku_rum.backend.domain.banner.application;

import ku_rum.backend.domain.banner.domain.Banner;
import ku_rum.backend.domain.banner.domain.repository.BannerRepository;
import ku_rum.backend.domain.banner.dto.BannerCreateRequest;
import ku_rum.backend.domain.banner.dto.BannerResponse;
import ku_rum.backend.domain.banner.dto.BannerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BannerService {

    private static final int MAX_BANNER_COUNT = 4;

    private final BannerRepository bannerRepository;

    public void create(BannerCreateRequest req) {
        long activeCount = bannerRepository.countByActiveTrue();
        if (activeCount >= MAX_BANNER_COUNT) {
            throw new IllegalStateException("활성 배너는 최대 4개까지 가능합니다.");
        }

        int order = req.getDisplayOrder();
        if (order < 1 || order > MAX_BANNER_COUNT) {
            throw new IllegalArgumentException("displayOrder는 1~4만 가능합니다.");
        }

        List<Banner> targets =
                bannerRepository.findByActiveTrueAndDisplayOrderGreaterThanEqual(order);

        targets.stream()
                .sorted(Comparator.comparingInt(Banner::getDisplayOrder).reversed())
                .forEach(b -> b.changeDisplayOrder(b.getDisplayOrder() + 1));

        Banner banner = Banner.builder()
                .title(req.getTitle())
                .imageUrl(req.getImageUrl())
                .linkUrl(req.getLinkUrl())
                .displayOrder(order)
                .startAt(req.getStartAt())
                .endAt(req.getEndAt())
                .build();

        bannerRepository.save(banner);
    }

    public void update(Long id, BannerUpdateRequest req) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배너 없음"));
        banner.update(req);
    }

    public void delete(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("배너 없음"));
        banner.deactivate();
    }

    @Transactional(readOnly = true)
    public List<BannerResponse> getActiveBanners() {
        return bannerRepository.findActiveBanners(LocalDateTime.now())
                .stream()
                .limit(MAX_BANNER_COUNT)
                .map(BannerResponse::from)
                .toList();
    }
}
