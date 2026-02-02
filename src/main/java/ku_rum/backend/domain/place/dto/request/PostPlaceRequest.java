package ku_rum.backend.domain.place.dto.request;

import java.math.BigDecimal;
import java.util.List;
import ku_rum.backend.domain.place.domain.CategoryChip;
import org.springframework.web.multipart.MultipartFile;

public record PostPlaceRequest(CategoryChip categoryChip, String name, String subName, String content,
                               BigDecimal latitude, BigDecimal longitude, List<MultipartFile> images) {
}
