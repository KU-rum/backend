package ku_rum.backend.domain.alarm.application;

import static ku_rum.backend.domain.alarm.util.FcmUtil.MESSAGE_TITLE;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.UNSUPPORTED_DIRECT_FCM;

import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.Announcement;
import ku_rum.backend.domain.alarm.dto.FcmDirectDto;
import ku_rum.backend.domain.alarm.dto.FcmTopicDto;
import ku_rum.backend.domain.alarm.util.FcmUtil;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.exception.global.GlobalException;
import org.springframework.stereotype.Component;

@Component
public class NewNoticeHandler implements AlarmMessageHandler {

    @Override
    public Alarm create(AlarmType alarmType, Object object, User user) {
        return null;
    }

    @Override
    public Announcement create(AlarmType alarmType, Object object) {
        return Announcement.builder()
                .alarmType(AlarmType.NEW_NOTICE)
                .message(getMessage())
                .build();
    }

    @Override
    public FcmTopicDto getFcmTopicDto(Object object) {
        return FcmTopicDto.builder()
                .title(MESSAGE_TITLE)
                .body(getMessage())
                .topic(FcmUtil.TOPIC_NAME)
                .build();
    }

    @Override
    public FcmDirectDto getFcmDirectDto(Object object, User user) {
        throw new GlobalException(UNSUPPORTED_DIRECT_FCM);
    }

    @Override
    public String getDataId(Object object) {
        Notice notice = (Notice) object;
        return String.valueOf(notice.getId());
    }

    public String getMessage() {
        return "새로운 공지가 올라왔어요. 바로 확인해보세요!";
    }
}
