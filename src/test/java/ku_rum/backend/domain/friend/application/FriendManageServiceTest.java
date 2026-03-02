package ku_rum.backend.domain.friend.application;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.List;
import ku_rum.backend.domain.alarm.application.FcmService;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.repository.AlarmRepository;
import ku_rum.backend.domain.friend.dto.request.FriendRequest;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.utill.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FriendManageServiceTest {

    @Autowired
    private FriendManageService friendManageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @MockBean
    private UserUtil userUtil;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private FcmService fcmService;

    private User fromUser;
    private User toUser;

    @BeforeEach
    void setUp() {
        fromUser = User.builder()
                .loginId("fromUser123")
                .email("from@konkuk.ac.kr")
                .nickname("요청보낸사람")
                .password("password123")
                .studentId("202100001")
                .build();

        toUser = User.builder()
                .loginId("toUser123")
                .email("to@konkuk.ac.kr")
                .nickname("요청받은사람")
                .password("password123")
                .studentId("202100002")
                .build();

        userRepository.save(fromUser);
        userRepository.save(toUser);
    }

    @Test
    @DisplayName("친구 요청 시 알림이 올바른 수신자에게 올바른 메시지로 생성된다")
    void requestFriend_createsAlarmWithCorrectReceiverAndMessage() {
        // given
        BDDMockito.given(userUtil.getUser()).willReturn(fromUser);
        FriendRequest request = new FriendRequest(toUser.getId());

        // when
        friendManageService.requestFriend(request);

        // then
        List<Alarm> alarms = alarmRepository.findAll();
        assertThat(alarms).hasSize(1);

        Alarm alarm = alarms.get(0);

        // 알림 수신자 검증: toUser가 알림을 받아야 함
        assertThat(alarm.getUser().getId()).isEqualTo(toUser.getId());

        // 알림 메시지 내용 검증: fromUser의 닉네임이 포함되어야 함
        String expectedMessage = String.format("%s 님이 친구 신청을 했어요. 친구 신청을 수락하시겠어요?", fromUser.getNickname());
        assertThat(alarm.getMessage()).isEqualTo(expectedMessage);

        // 알림 타입 검증
        assertThat(alarm.getAlarmType()).isEqualTo(AlarmType.NEW_FRIEND_REQUEST);

        // 알림 확인 상태 검증 (초기값은 false)
        assertThat(alarm.isChecked()).isFalse();
    }
}