package ku_rum.backend.domain.alarm.application;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmCategory;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.alarm.domain.UserAnnouncement;
import ku_rum.backend.domain.alarm.domain.repository.AlarmRepository;
import ku_rum.backend.domain.alarm.domain.repository.AnnouncementRepository;
import ku_rum.backend.domain.alarm.domain.repository.UserAnnouncementRepository;
import ku_rum.backend.domain.alarm.dto.AlarmCursorDto;
import ku_rum.backend.domain.alarm.dto.request.PatchAlarmRequest;
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

    private static final int LAST_KNOWN_LENGTH = 2;
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
        AlarmCursorDto alarmCursorDto = getAlarmCursorDto(request.lastKnown());

        User user = userService.getUser();
        Pageable pageable = PageRequest.of(0, request.limit() + 1);
        List<Alarm> alarms = alarmRepository.findAlarms(user, alarmCursorDto.lastAlarmId(), pageable);
        List<UserAnnouncement> userAnnouncements = userAnnouncementRepository.findUserAnnouncement(user,
                alarmCursorDto.lastAnnouncementId(), pageable);

        List<GetAlarmDto> merged = Stream.concat(
                        alarms.stream().map(GetAlarmDto::from),
                        userAnnouncements.stream().map(GetAlarmDto::from)
                )
                .sorted(Comparator.comparing(GetAlarmDto::createdAt).reversed())
                .limit(request.limit())
                .toList();

        int totalFetched = alarms.size() + userAnnouncements.size();
        boolean hasNext = totalFetched > request.limit();
        String nextCursor = null;
        if (hasNext && !merged.isEmpty()) {
            GetAlarmDto lastItem = merged.get(merged.size() - 1);

            nextCursor = getNextCursor(alarmCursorDto, alarms, userAnnouncements, lastItem);
        }
        return new GetAlarmResponse(merged, hasNext, nextCursor);
    }

    @Transactional
    public PatchAlarmResponse patchUserAlarm(CustomUserDetails userDetails, PatchAlarmRequest request) {
        Long userId = userService.getUser().getId();
        if (request.alarmCategory().equals(AlarmCategory.ALARM)) {
            return patchAlarm(userId, request.alarmId());
        }
        return patchAnnouncement(userId, request.alarmId());
    }

    private PatchAlarmResponse patchAlarm(Long userId, Long alarmId) {
        Alarm alarm = findAlarmById(alarmId);

        if (!userId.equals(alarm.getUser().getId())) {
            throw new GlobalException(BaseExceptionResponseStatus.UNAUTHORIZED_ALARM);
        }
        alarm.checkAlarm();

        return PatchAlarmResponse.from(alarm);
    }

    private PatchAlarmResponse patchAnnouncement(Long userId, Long announcementId) {
        UserAnnouncement userAnnouncement = findAnnouncementById(announcementId);

        if (!userId.equals(userAnnouncement.getUser().getId())) {
            throw new GlobalException(BaseExceptionResponseStatus.UNAUTHORIZED_ALARM);
        }
        userAnnouncement.checkAlarm();

        return PatchAlarmResponse.from(userAnnouncement);
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

    private Alarm findAlarmById(Long alarmId) {
        return alarmRepository.findById(alarmId).orElseThrow(
                () -> new GlobalException(BaseExceptionResponseStatus.ALARM_NOT_FOUND));
    }

    private UserAnnouncement findAnnouncementById(Long announcementId) {
        return userAnnouncementRepository.findById(announcementId).orElseThrow(
                () -> new GlobalException(BaseExceptionResponseStatus.ALARM_NOT_FOUND));
    }

    private String getNextCursor(AlarmCursorDto alarmCursorDto, List<Alarm> alarms,
                                 List<UserAnnouncement> userAnnouncements, GetAlarmDto lastItem) {
        Long nextAlarmId = alarms.stream()
                .filter(a -> a.getCreatedAt().isBefore(lastItem.createdAt()))
                .map(Alarm::getId)
                .max(Long::compareTo)
                .orElse(alarmCursorDto.lastAlarmId());

        Long nextAnnouncementId = userAnnouncements.stream()
                .filter(ua -> ua.getAnnouncement().getCreatedAt().isBefore(lastItem.createdAt()))
                .map(UserAnnouncement::getId)
                .max(Long::compareTo)
                .orElse(alarmCursorDto.lastAnnouncementId());

        return nextAlarmId + "_" + nextAnnouncementId;
    }

    private AlarmCursorDto getAlarmCursorDto(String lastKnown) {
        Long lastAlarmId = null;
        Long lastAnnouncementId = null;

        if (lastKnown != null) {
            String[] parts = lastKnown.split("_");
            if (LAST_KNOWN_LENGTH != 2) {
                throw new GlobalException(BaseExceptionResponseStatus.INVALID_CURSOR_FORMAT);
            }
            try {
                lastAlarmId = Long.valueOf(parts[0]);
                lastAnnouncementId = Long.valueOf(parts[1]);
            } catch (NumberFormatException e) {
                throw new GlobalException(BaseExceptionResponseStatus.INVALID_CURSOR_FORMAT);
            }
        }
        return new AlarmCursorDto(lastAlarmId, lastAnnouncementId);
    }
}
