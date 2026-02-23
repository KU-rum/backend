package ku_rum.backend.domain.banner.dto;

import java.util.List;
import java.util.stream.IntStream;
import org.springframework.web.multipart.MultipartFile;

public record PatchBannerRequests(List<PatchBannerRequest> banners) {

    public static PatchBannerRequests of(List<MultipartFile> images, List<String> links) {
        List<PatchBannerRequest> banners = IntStream.range(0, images.size())
                .mapToObj(i -> new PatchBannerRequest(images.get(i), links.get(i)))
                .toList();
        return new PatchBannerRequests(banners);
    }
}
