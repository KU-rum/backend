package ku_rum.backend.domain.banner.presentation;

import java.util.List;
import ku_rum.backend.domain.banner.application.BannerService;
import ku_rum.backend.domain.banner.dto.GetBannerResponse;
import ku_rum.backend.domain.banner.dto.PatchBannerRequests;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/banner")
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public BaseResponse<List<GetBannerResponse>> findBanners() {
        List<GetBannerResponse> response = bannerService.findBanners();
        return BaseResponse.ok(response);
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<Void> addBanners(
            @RequestParam("images") List<MultipartFile> bannerImages,
            @RequestParam("links") List<String> bannerLinks) {
        PatchBannerRequests request = PatchBannerRequests.of(bannerImages, bannerLinks);
        bannerService.addBanner(request);
        return BaseResponse.ok();
    }

    @DeleteMapping({"/{bannerId}"})
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<Void> deleteBanners(@PathVariable("bannerId") Long bannerId) {
        bannerService.deleteById(bannerId);
        return BaseResponse.ok();
    }
}
