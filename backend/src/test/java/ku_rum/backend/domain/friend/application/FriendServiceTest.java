package ku_rum.backend.domain.friend.application;

import jakarta.transaction.Transactional;
import ku_rum.backend.domain.building.domain.Building;
import ku_rum.backend.domain.building.domain.repository.BuildingRepository;
import ku_rum.backend.domain.department.domain.Department;
import ku_rum.backend.domain.department.domain.repository.DepartmentRepository;
import ku_rum.backend.domain.friend.domain.Friend;
import ku_rum.backend.domain.friend.domain.repository.FriendRepository;
import ku_rum.backend.domain.friend.dto.request.FriendFindRequest;
import ku_rum.backend.domain.friend.dto.request.FriendListRequest;
import ku_rum.backend.domain.friend.dto.response.FriendFindResponse;
import ku_rum.backend.domain.friend.dto.response.FriendListResponse;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.exception.friend.NoFriendsException;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FriendServiceTest {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private FriendService friendService;


    @BeforeAll
    void init() {
        Building building = Building.of("신공학관",11L, "신공", BigDecimal.valueOf(64.3423423), BigDecimal.valueOf(64.3423423));
        buildingRepository.save(building);

        Department department = Department.of("컴퓨터공학부", building);
        departmentRepository.save(department);

        User fromUser = User.of("kmw106933", "미미미누", "password123", "202112322", department);
        User toUser1 = User.of("kmw106934", "미미미누1", "password123", "202112321", department);
        User toUser2 = User.of("kmw106935", "미미미누2", "password123", "202112323", department);
        User newUser = User.of("kmw106936", "미미미누3", "password123", "202112324", department);

        userRepository.save(fromUser);
        userRepository.save(toUser1);
        userRepository.save(toUser2);
        userRepository.save(newUser);

        Friend friend = Friend.of(fromUser, toUser1);
        Friend friend2 = Friend.of(fromUser, toUser2);

        friendRepository.save(friend);
        friendRepository.save(friend2);
    }


    @Test
    @DisplayName("기존 유저의 친구 정보를 가져온다 - 성공")
    void getFriendsLists() {
        //given
        FriendListRequest friendListRequest = FriendListRequest.from(1L);

        //when
        List<FriendListResponse> myLists = friendService.getMyLists(friendListRequest);

        //then
        assertThat(myLists).hasSize(2)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(2L, "미미미누1"),
                        Tuple.tuple(3L, "미미미누2")
                );

    }

    @Test
    @DisplayName("내 친구 목록에 친구가 없는 경우 예외를 발생시킨다.")
    void getFriendListsWithNoFriends() {
        //given
        FriendListRequest friendListRequest = FriendListRequest.from(2L);

        //when
        assertThatThrownBy(() -> friendService.getMyLists(friendListRequest))
                .isInstanceOf(NoFriendsException.class)
                .hasMessage("친구 목록에 친구가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("내 친구 목록에 존재하는 친구를 검색한다 - 성공")
    void findAFriendInMyFriendLists() {
        //given
        FriendFindRequest friendFindRequest = FriendFindRequest.of(1L, "미미미누1");

        //when
        FriendFindResponse friendFIndResponse = friendService.findByNameInLists(friendFindRequest);

        //then
        Assertions.assertThat(friendFIndResponse.id()).isEqualTo(2L);
        Assertions.assertThat(friendFIndResponse.nickname()).isEqualTo("미미미누1");
    }

    @Test
    @DisplayName("내 친구 목록에 존재하는 않는 친구를 검색한다 - 실패")
    void findNoAFriendInMyFriendLists() {
        //given
        FriendFindRequest friendFindRequest = FriendFindRequest.of(2L, "미미미누3");

        //when
        assertThatThrownBy(() -> friendService.findByNameInLists(friendFindRequest))
                .isInstanceOf(NoFriendsException.class)
                .hasMessage("친구 목록에 친구가 존재하지 않습니다.");
    }

}