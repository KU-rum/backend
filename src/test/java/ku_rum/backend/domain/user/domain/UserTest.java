package ku_rum.backend.domain.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ku_rum.backend.domain.college.domain.College;
import ku_rum.backend.domain.department.domain.Department;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {
    @DisplayName("유저 생성 시 학과 정보를 넣어준다.")
    @Test
    void registeredUserWithDepartment() {
        //given
        Department department = getDepartment();

        //when
        User user = User.of("사용자1", "kmw106933@konkuk.ac.kr", "미미미누", "password123", "202112322", department,
                AgreementStatus.AGREED, null);

        //then
        assertThat(user.getNickname()).isEqualTo("미미미누");
        assertThat(user.getStudentId()).isEqualTo("202112322");
        //assertThat(user.getDepartment()).isEqualTo(department);
    }

    private Department getDepartment() {
        String Deptname = "컴퓨터공학부";
        College college = College.of("공과대학");
        String url = "url";
        Department department = Department.of(Deptname, college, url);
        return department;
    }
}