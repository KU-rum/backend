package ku_rum.backend.domain.user.dto.response;

public record S3PresignedUrlResponse(
        String presignedUrl,
        String fileKey,
        String fullUrl
) {
    public static S3PresignedUrlResponse of(String presignedUrl, String fileKey, String fullUrl) {
        return new S3PresignedUrlResponse(presignedUrl, fileKey, fullUrl);
    }
}