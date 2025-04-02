package ku_rum.backend.domain.common.image.presentation;

import ku_rum.backend.domain.common.image.application.ImageStorageService;
import ku_rum.backend.domain.common.image.dto.response.ImageResponse;
import ku_rum.backend.global.support.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageStorageService imageStorageService;

    @GetMapping("/{fileName}")
    public BaseResponse<ImageResponse> getUrl(@PathVariable(name = "fileName") String fileName) {
        return BaseResponse.ok(imageStorageService.getPresignedUrl(fileName));
    }
}
