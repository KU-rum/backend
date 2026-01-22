package ku_rum.backend.domain.department.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ku_rum.backend.domain.college.domain.College;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DepartmentTest {

    @DisplayName("학과 생성 시 학과 이름을 넣어준다.")
    @Test
    void registeredDepartmentWithName() {
        //given
        College college = College.of("공과대학");
        String Deptname = "컴퓨터공학부";

        //when
        Department department = Department.of(Deptname, college, "url");

        //then
        assertThat(department.getName()).isEqualTo(Deptname);
    }
}