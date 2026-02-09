package ku_rum.backend.domain.banner.presentation;

import ku_rum.backend.domain.banner.application.BannerService;
import ku_rum.backend.domain.banner.dto.BannerCreateRequest;
import ku_rum.backend.domain.banner.dto.BannerResponse;
import ku_rum.backend.domain.banner.dto.BannerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @PostMapping("/admin/banners")
    public void create(@RequestBody BannerCreateRequest request) {
        bannerService.create(request);
    }

    @PutMapping("/admin/banners/{id}")
    public void update(@PathVariable Long id,
                       @RequestBody BannerUpdateRequest request) {
        bannerService.update(id, request);
    }

    @DeleteMapping("/admin/banners/{id}")
    public void delete(@PathVariable Long id) {
        bannerService.delete(id);
    }


    @GetMapping("/banners")
    public List<BannerResponse> getBanners() {
        return bannerService.getActiveBanners();
    }
}
