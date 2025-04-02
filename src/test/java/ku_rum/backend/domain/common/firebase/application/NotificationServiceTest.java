package ku_rum.backend.domain.common.firebase.application;

import ku_rum.backend.global.utill.RedisUtil;
import ku_rum.backend.global.utill.UserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NotificationServiceTest {

    @Mock
    private RedisUtil redisUtil;  // RedisUtil Mock 객체

    @InjectMocks
    private NotificationService notificationService;  // 테스트 대상 객체

    MockedStatic<UserUtil> userUtilsMockedStatic;

    private static final Long TEST_USER_ID = 123L;
    private static final String TEST_TOKEN = "sample-token";

    @BeforeEach
    void setup() {
        userUtilsMockedStatic = Mockito.mockStatic(UserUtil.class);
        userUtilsMockedStatic.when(UserUtil::getLongMemberId).thenReturn(TEST_USER_ID);
    }
    @AfterEach
    public void tearDown() {
        userUtilsMockedStatic.close();
    }

    @Test
    void testRegister() {
        // given
        Long memberId = TEST_USER_ID;
        String token = TEST_TOKEN;

        // when
        notificationService.register(token);

        // then
        verify(redisUtil, times(1)).setRedisData("notification:token:" + memberId, token);  // Redis에 저장이 한 번 호출되었는지 검증
    }

    @Test
    void testDeleteToken() {
        // given
        Long userId = TEST_USER_ID;

        // when
        notificationService.deleteToken(userId);

        // then
        verify(redisUtil, times(1)).deleteRedisData("notification:token:" + userId);  // Redis에서 삭제가 한 번 호출되었는지 검증
    }
}