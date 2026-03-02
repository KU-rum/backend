package ku_rum.backend.domain.banner.dto;

import java.util.List;
import java.util.stream.IntStream;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import org.springframework.web.multipart.MultipartFile;

public record PatchBannerRequests(List<PatchBannerRequest> banners) {

    public static PatchBannerRequests of(List<MultipartFile> images, List<String> links) {
        if (images == null || links == null) {
            throw new GlobalException(BaseExceptionResponseStatus.BANNER_REQUEST_NULL);
        }
        if (images.size() != links.size()) {
            throw new GlobalException(BaseExceptionResponseStatus.BANNER_IMAGE_LINK_COUNT_MISMATCH);
        }
        List<PatchBannerRequest> banners = IntStream.range(0, images.size())
                .mapToObj(i -> new PatchBannerRequest(images.get(i), links.get(i)))
                .toList();
        return new PatchBannerRequests(banners);
    }
}
