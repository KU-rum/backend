package ku_rum.backend.domain.alarm.dto.response;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;

public record AlarmPaginationRequest(@Nullable String lastKnown,
                                     @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
                                     int limit) {
}
