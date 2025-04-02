package ku_rum.backend.domain.friend.application;

import ku_rum.backend.domain.building.domain.Building;
import ku_rum.backend.domain.department.domain.Department;
import ku_rum.backend.domain.friend.domain.Friend;
import ku_rum.backend.domain.friend.domain.FriendStatus;
import ku_rum.backend.domain.friend.domain.repository.FriendRepository;
import ku_rum.backend.domain.friend.dto.response.FriendListResponse;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.AgreementStatus;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.exception.friend.NoFriendsException;
import ku_rum.backend.global.utill.UserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendService friendService;

    private User user;
    private User friend;
    private Friend friendEntity;
    MockedStatic<UserUtil> userUtilsMockedStatic;

    @BeforeEach
    void setUp() {
        BigDecimal latitude = BigDecimal.valueOf(64.3423423);
        BigDecimal longitude = BigDecimal.valueOf(342.2343434);

        Building building = Building.of("신공학관", 3L,"신공", 1L, latitude, longitude);
        user = User.of("user1", "user1@example.com", "nickname1", "password", "123456", Department.of("CS", building), AgreementStatus.AGREED);
        friend = User.of("user2", "user2@example.com", "nickname2", "password", "654321", Department.of("Math", building), AgreementStatus.AGREED);
        friendEntity = Friend.of(user, friend, FriendStatus.PENDING);

        userUtilsMockedStatic = Mockito.mockStatic(UserUtil.class);
        userUtilsMockedStatic.when(UserUtil::getLongMemberId).thenReturn(1L);
    }

    @AfterEach
    public void tearDown() {
        userUtilsMockedStatic.close();
    }

    @Test
    @DisplayName("친구 조회시 모든 친구 조회 - 성공")
    void getMyLists_ShouldReturnFriendList() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(user));
        when(friendRepository.findFriends(
                eq(FriendStatus.ACCEPT), any())).thenReturn(List.of(friendEntity));

        List<FriendListResponse> result = friendService.getMyLists();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).nickname()).isEqualTo("nickname2");
    }

    @Test
    @DisplayName("친구 요청 시 친구를 저장한다 - 성공")
    void requestFriends_ShouldSaveFriendRequest() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(user)).thenReturn(Optional.of(friend));
        when(friendRepository.existFriends(FriendStatus.ACCEPT, user.getId(), friend.getId())).thenReturn(false);

        friendService.requestFriends(1L);

        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    @DisplayName("친구 요청 시 이미 친구가 있는 경우 - 예외 발생")
    void requestFriends_ShouldThrowExceptionIfAlreadyFriends() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(user)).thenReturn(Optional.of(friend));
        when(friendRepository.existFriends(FriendStatus.ACCEPT, user.getId(), friend.getId())).thenReturn(true);

        assertThatThrownBy(() -> friendService.requestFriends(1L))
                .isInstanceOf(NoFriendsException.class);
    }

    @Test
    @DisplayName("친구 수락 시 상태를 ACCEPT로 변환한다.")
    void acceptFriendRequest_ShouldUpdateStatusToAccept() {
        lenient().when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(user));
        lenient().when(friendRepository.findFirstByFromUserAndToUserAndStatus(
                        any(User.class), any(User.class), eq(FriendStatus.PENDING)))
                .thenReturn(Optional.of(friendEntity));

        friendService.acceptFriendRequest(1L);

        assertThat(friendEntity.getStatus()).isEqualTo(FriendStatus.ACCEPT);
    }

    @Test
    @DisplayName("친구를 삭제하는 경우 - 상태를 Reject으로 변경한다.")
    void deleteFriendRequest_ShouldUpdateStatusToReject() {
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(user)).thenReturn(Optional.of(friend));
        when(friendRepository.findOriginFriends(FriendStatus.ACCEPT, user.getId(), friend.getId()))
                .thenReturn(List.of(friendEntity));

        friendService.deleteFriendRequest(2L);

        assertThat(friendEntity.getStatus()).isEqualTo(FriendStatus.REJECT);
    }
}
