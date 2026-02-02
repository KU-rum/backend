package ku_rum.backend.domain.common.image.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import ku_rum.backend.domain.common.image.domain.vo.FilePath;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ImageService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private static final String PREFIX = "place-images";

    public List<String> uploadImages(List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            String imageUrl = uploadImage(image);
            imageUrls.add(imageUrl);
        }

        return imageUrls;
    }

    public String uploadImage(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        String filePath = FilePath.createPath(PREFIX, originalFilename);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .contentType(image.getContentType())
                    .contentLength(image.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
        } catch (IOException e) {
            log.error("이미지 업로드 실패: {}", e.getMessage());
            throw new GlobalException(BaseExceptionResponseStatus.IMAGE_UPLOAD_FAILED);
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, filePath);
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        int prefixIndex = imageUrl.indexOf(PREFIX);
        if (prefixIndex == -1) {
            log.warn("삭제할 수 없는 이미지 URL 형식: {}", imageUrl);
            return;
        }

        String key = imageUrl.substring(prefixIndex);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    public void deleteImages(List<String> imageUrls) {
        for (String url : imageUrls) {
            try {
                deleteImage(url);
            } catch (Exception e) {
                log.warn("Failed to delete S3 image: {}", url, e);
            }
        }
    }
}