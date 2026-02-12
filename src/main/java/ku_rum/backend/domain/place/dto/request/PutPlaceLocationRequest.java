package ku_rum.backend.domain.place.dto.request;

import java.math.BigDecimal;

public record PutPlaceLocationRequest(BigDecimal latitude, BigDecimal longitude) {
}