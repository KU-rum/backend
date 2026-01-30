package ku_rum.backend.domain.place.dto.request;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record PutPlaceImagesRequest(List<MultipartFile> images) {
}
