package ku_rum.backend.domain.alarm.dto.response;

import java.time.LocalDateTime;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmCategory;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.alarm.domain.UserAnnouncement;
import lombok.Builder;

@Builder
public record GetAlarmDto(Long id, AlarmType alarmType, AlarmCategory alarmCategory, String message, boolean isChecked,
                          String dataId,
                          LocalDateTime createdAt) {

    public static GetAlarmDto from(Alarm alarm) {
        return GetAlarmDto.builder()
                .id(alarm.getId())
                .alarmType(alarm.getAlarmType())
                .alarmCategory(alarm.getAlarmType().getAlarmCategory())
                .message(alarm.getMessage())
                .isChecked(alarm.isChecked())
                .dataId(alarm.getDataId())
                .createdAt(alarm.getCreatedAt())
                .build();
    }

    public static GetAlarmDto from(UserAnnouncement userAnnouncement) {
        Announcement announcement = userAnnouncement.getAnnouncement();
        return GetAlarmDto.builder()
                .id(announcement.getId())
                .alarmType(announcement.getAlarmType())
                .alarmCategory(userAnnouncement.getAnnouncement().getAlarmType().getAlarmCategory())
                .message(announcement.getMessage())
                .dataId(userAnnouncement.getDataId())
                .createdAt(userAnnouncement.getCreatedAt())
                .build();
    }
}
