package ku_rum.backend.domain.place.dto.request;

import java.math.BigDecimal;
import ku_rum.backend.domain.place.domain.CategoryChip;

public record PostPlaceRequest(CategoryChip categoryChip, String name, String subName, String content,
                               BigDecimal latitude, BigDecimal longitude) {
}
