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
import ku_rum.backend.domain.alarm.dto.response.AlarmPaginationRequest;
import ku_rum.backend.domain.alarm.dto.response.GetAlarmDto;
import ku_rum.backend.domain.alarm.dto.response.GetAlarmResponse;
import ku_rum.backend.domain.alarm.dto.response.PatchAlarmResponse;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final UserService userService;

    @Transactional
    public void notifyAlarm(AlarmType alarmType, Object object, User user) {
        AlarmMessageHandler alarmMessageHandler = alarmMessageHandlers.get(alarmType);
        if (alarmMessageHandler == null) {
            throw new IllegalArgumentException("지원하지 않는 알림 타입입니다: " + alarmType);
        }

        Alarm alarm = alarmMessageHandler.create(alarmType, object, user);

        alarmRepository.save(alarm);
    }

    @Transactional
    public void notifyAlarm(AlarmType alarmType, Object object) {
        AlarmMessageHandler alarmMessageHandler = alarmMessageHandlers.get(alarmType);
        if (alarmMessageHandler == null) {
            throw new IllegalArgumentException("지원하지 않는 알림 타입입니다: " + alarmType);
        }

        Announcement announcement = alarmMessageHandler.create(alarmType, object);
        Announcement saveAnnouncement = announcementRepository.save(announcement);
        saveUserAnnouncement(saveAnnouncement);
    }

    public GetAlarmResponse getAlarmResponse(CustomUserDetails userDetails, AlarmPaginationRequest request) {
        User user = userService.getUser();
        Pageable pageable = PageRequest.of(0, request.limit() + 1);
        Long lastId = Long.valueOf(request.lastKnown());
        List<Alarm> alarms = alarmRepository.findAlarms(user, lastId, pageable);

        boolean hasNext = alarms.size() > request.limit();

        String nextCursor = null;
        if (hasNext) {
            Alarm lastItem = alarms.get(
                    alarms.size() - 1);
            nextCursor = String.valueOf(lastItem.getId());
        }
        List<GetAlarmDto> getAlarmDtos = alarms.stream()
                .map(GetAlarmDto::from)
                .toList();

        return new GetAlarmResponse(getAlarmDtos, hasNext, nextCursor);
    }

    @Transactional
    public PatchAlarmResponse patchUserAlarm(CustomUserDetails userDetails, Long alarmId) {
        Long userId = userService.getUser().getId();
        Alarm alarm = findById(alarmId);

        if (!userId.equals(alarm.getUser().getId())) {
            throw new GlobalException(BaseExceptionResponseStatus.UNAUTHORIZED_ALARM);
        }
        alarm.checkAlarm();

        return PatchAlarmResponse.from(alarm);
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

    private Alarm findById(Long alarmId) {
        return alarmRepository.findById(alarmId).orElseThrow(
                () -> new GlobalException(BaseExceptionResponseStatus.ALARM_NOT_FOUND));
    }
}
