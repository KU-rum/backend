package ku_rum.backend.domain.alarm.application;


import java.util.Map;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.alarm.domain.repository.AlarmRepository;
import ku_rum.backend.domain.alarm.domain.repository.AnnouncementRepository;
import ku_rum.backend.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final Map<AlarmType, AlarmMessageHandler> alarmMessageHandlers;
    private final AlarmRepository alarmRepository;
    private final AnnouncementRepository announcementRepository;

    @Transactional
    public void notifyAlarm(AlarmType alarmType, Object object, User user) {
        AlarmMessageHandler alarmMessageHandler = alarmMessageHandlers.get(alarmType);
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
        String message = alarmMessageHandler.create(object);

        Announcement announcement = Announcement.builder()
                .alarmType(alarmType)
                .message(message)
                .isChecked(false)
                .build();
        announcementRepository.save(announcement);
    }
}
