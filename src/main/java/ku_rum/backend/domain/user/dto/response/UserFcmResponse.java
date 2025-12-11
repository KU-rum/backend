package ku_rum.backend.domain.user.dto.response;

import ku_rum.backend.domain.user.domain.DeviceType;
import ku_rum.backend.domain.user.domain.UserFcmToken;
import lombok.Builder;

@Builder
public record UserFcmResponse(Long id, String token, DeviceType deviceType) {

    public static UserFcmResponse from(UserFcmToken userFcmToken) {
        return UserFcmResponse.builder()
                .id(userFcmToken.getId())
                .token(userFcmToken.getToken())
                .deviceType(userFcmToken.getDeviceType())
                .build();
    }
}
