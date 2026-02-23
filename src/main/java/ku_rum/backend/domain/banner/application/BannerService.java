package ku_rum.backend.domain.banner.application;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.URL_NOT_BANNER;

import java.util.List;
import ku_rum.backend.domain.banner.domain.Banner;
import ku_rum.backend.domain.banner.domain.repository.BannerRepository;
import ku_rum.backend.domain.banner.dto.GetBannerResponse;
import ku_rum.backend.domain.banner.dto.PatchBannerRequests;
import ku_rum.backend.domain.common.image.application.S3ImageService;
import ku_rum.backend.global.exception.global.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class BannerService {

    private final BannerRepository bannerRepository;
    private final S3ImageService s3ImageService;

    public List<GetBannerResponse> findBanners() {
        List<Banner> banners = bannerRepository.findAll();
        return banners.stream()
                .map(banner -> new GetBannerResponse(banner.getBannerId(), banner.getImageUrl(),
                        banner.getLink()))
                .toList();
    }

    @Transactional
    public void addBanner(PatchBannerRequests requests) {
        List<Banner> banners = requests.banners().stream()
                .map(request -> {
                    String imageUrl = s3ImageService.uploadBannerImage(request.images());
                    return Banner.builder()
                            .imageUrl(imageUrl)
                            .link(request.link())
                            .build();
                })
                .toList();
        bannerRepository.saveAll(banners);
    }

    @Transactional
    public void deleteById(Long bannerId) {
        Banner banner = findById(bannerId);
        bannerRepository.deleteById(bannerId);

        s3ImageService.deleteBannerImage(banner.getImageUrl());
    }

    private Banner findById(Long bannerId) {
        return bannerRepository.findById(bannerId)
                .orElseThrow(() -> new GlobalException(URL_NOT_BANNER));
    }
}
