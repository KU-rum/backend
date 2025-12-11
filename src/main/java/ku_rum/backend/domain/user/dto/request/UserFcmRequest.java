package ku_rum.backend.domain.user.dto.request;

import ku_rum.backend.domain.user.domain.DeviceType;

public record UserFcmRequest(String token, DeviceType deviceType) {
}
