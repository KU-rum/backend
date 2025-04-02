package ku_rum.backend.domain.common.image.dto.response;

public record ImageResponse(String preSignedUrl, String imageUrl) {

    public static ImageResponse from(String preSignedUrl, String imageUrl) {
        return new ImageResponse(preSignedUrl, imageUrl);
    }
}
