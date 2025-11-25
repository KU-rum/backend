package ku_rum.backend.domain.alarm.application;


import java.util.List;
import java.util.Map;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.alarm.domain.UserAnnouncement;
import ku_rum.backend.domain.alarm.domain.repository.AlarmRepository;
import ku_rum.backend.domain.alarm.domain.repository.AnnouncementRepository;
import ku_rum.backend.domain.alarm.domain.repository.UserAnnouncementRepository;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final Map<AlarmType, AlarmMessageHandler> alarmMessageHandlers;
    private final AlarmRepository alarmRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserAnnouncementRepository userAnnouncementRepository;
    private final UserRepository userRepository;

    @Transactional
    public void notifyAlarm(AlarmType alarmType, Object object, User user) {
        AlarmMessageHandler alarmMessageHandler = alarmMessageHandlers.get(alarmType);
        if (alarmMessageHandler == null) {
            throw new IllegalArgumentException("지원하지 않는 알림 타입입니다: " + alarmType);
        }

        String message = alarmMessageHandler.create(object);

        Alarm alarm = Alarm.builder()
                .alarmType(alarmType)
                .message(message)
                .isChecked(false)
                .user(user)
                .build();
        alarmRepository.save(alarm);
    }

    @Transactional
    public void notifyAlarm(AlarmType alarmType, Object object) {
        AlarmMessageHandler alarmMessageHandler = alarmMessageHandlers.get(alarmType);
        if (alarmMessageHandler == null) {
            throw new IllegalArgumentException("지원하지 않는 알림 타입입니다: " + alarmType);
        }
        String message = alarmMessageHandler.create(object);

        Announcement announcement = Announcement.builder()
                .alarmType(alarmType)
                .message(message)
                .isChecked(false)
                .build();
        Announcement saveAnnouncement = announcementRepository.save(announcement);
        saveUserAnnouncement(saveAnnouncement);
    }

    private void saveUserAnnouncement(Announcement announcement) {
        List<UserAnnouncement> userAnnouncements = userRepository.findAll().stream()
                .map(user -> UserAnnouncement.builder()
                        .isChecked(false)
                        .user(user)
                        .announcement(announcement)
                        .build())
                .toList();
        userAnnouncementRepository.saveAll(userAnnouncements);
    }
}
