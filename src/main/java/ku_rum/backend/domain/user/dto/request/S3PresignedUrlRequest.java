package ku_rum.backend.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record S3PresignedUrlRequest(
        @NotBlank(message = "파일명은 필수입니다.")
        String fileName,

        @NotBlank(message = "파일 타입은 필수입니다.")
        String fileType
) {
}