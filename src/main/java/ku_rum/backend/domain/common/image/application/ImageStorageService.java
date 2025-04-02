package ku_rum.backend.domain.common.image.application;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import ku_rum.backend.domain.common.image.domain.vo.FilePath;
import ku_rum.backend.domain.common.image.dto.response.ImageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStorageService {
    @Value("${cloud.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private static final String prefix = "images";
    private static final long EXPIRATION_TIME_MILLIS = Duration.ofMinutes(2).toMillis();

    public ImageResponse getPresignedUrl(final String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, FilePath.createPath(prefix, fileName));
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        log.info("[S3FileService] getPutPreSignedUrl: {}", url.toString());
        String preSignedUrl = url.toString();
        return ImageResponse.from(preSignedUrl, getImageUrl(preSignedUrl));
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(presignedUrlExpiration());

        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        return generatePresignedUrlRequest;
    }

    private Date presignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime() + EXPIRATION_TIME_MILLIS;
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    private String getImageUrl(String presignedUrl) {
        return presignedUrl.split("\\?")[0];
    }
}
