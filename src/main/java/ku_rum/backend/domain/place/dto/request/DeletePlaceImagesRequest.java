package ku_rum.backend.domain.place.dto.request;

import java.util.List;

public record DeletePlaceImagesRequest(List<String> imageUrls) {
}
