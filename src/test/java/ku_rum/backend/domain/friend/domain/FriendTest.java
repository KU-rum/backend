package ku_rum.backend.domain.friend.domain;

import static ku_rum.backend.domain.friend.domain.vo.FriendStatus.ACCEPT;
import static org.assertj.core.api.Assertions.assertThat;

import ku_rum.backend.domain.college.domain.College;
import ku_rum.backend.domain.department.domain.Department;
import ku_rum.backend.domain.user.domain.AgreementStatus;
import ku_rum.backend.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FriendTest {

    @DisplayName("친구 생성 시, 팔로우/팔로잉 정보를 넣어준다.")
    @Test
    void registeredFriendWithFromUserAndToUser() {
        //given
        User fromUser = createUser("사용자1", "202112322");
        User toUser = createUser("사용자2", "202123232");

        //when
        Friend friend = Friend.of(fromUser, toUser, ACCEPT);

        //then
        assertThat(friend.getFromUser()).isEqualTo(fromUser);
        assertThat(friend.getToUser()).isEqualTo(toUser);
    }

    private User createUser(String username, String studentID) {
        College college = College.of("공과대학");
        Department department = Department.of("컴퓨터공학부", college, "url");
        return User.of(username, "kmw106933@konkuk.ac.kr", studentID, "password123", "202112322", department,
                AgreementStatus.AGREED, null);
    }
}