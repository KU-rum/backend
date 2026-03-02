package ku_rum.backend.domain.banner.dto;

import org.springframework.web.multipart.MultipartFile;

public record PatchBannerRequest(MultipartFile images, String link) {
}
