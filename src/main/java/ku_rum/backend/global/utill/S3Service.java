package ku_rum.backend.domain.common.s3.application;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * S3 presigned URL 생성 (프로필 이미지 업로드용)
     *
     * @param fileName 원본 파일명
     * @param fileType 파일 MIME 타입
     * @return presigned URL 정보
     */
    public PresignedUrlInfo generatePresignedUrl(String fileName, String fileType) {
        // 고유한 파일 키 생성
        String fileKey = generateFileKey(fileName);

        log.info("Presigned URL 생성 요청: fileName={}, fileType={}, fileKey={}", fileName, fileType, fileKey);

        // PutObjectRequest 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(fileType)
                .build();

        // Presigned URL 생성 (15분 유효)
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();

        // 업로드 후 접근할 최종 URL
        String fullUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileKey);

        log.info("Presigned URL 생성 완료: fileKey={}", fileKey);

        return new PresignedUrlInfo(presignedUrl, fileKey, fullUrl);
    }

    /**
     * 파일 키 생성 (profile/{UUID}_{fileName})
     */
    private String generateFileKey(String fileName) {
        String uuid = UUID.randomUUID().toString();
        String extension = extractExtension(fileName);
        return String.format("profile/%s_%s", uuid, fileName);
    }

    /**
     * 파일 확장자 추출
     */
    private String extractExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * Presigned URL 정보를 담는 레코드
     */
    public record PresignedUrlInfo(
            String presignedUrl,
            String fileKey,
            String fullUrl
    ) {
    }
}