package ku_rum.backend.domain.place.dto;

import ku_rum.backend.domain.user.domain.User;

public record UserPlaceAlarmDto(User user, String placeName) {
}
