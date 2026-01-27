package ku_rum.backend.domain.common.image.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ku_rum.backend.domain.common.image.domain.vo.FilePath;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ImageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

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

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket,
                    filePath,
                    image.getInputStream(),
                    metadata
            );
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("이미지 업로드 실패: {}", e.getMessage());
            throw new GlobalException(BaseExceptionResponseStatus.IMAGE_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(bucket, filePath).toString();
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
        amazonS3.deleteObject(bucket, key);
    }
}