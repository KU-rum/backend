package ku_rum.backend.domain.user.application;

import jakarta.transaction.Transactional;
import ku_rum.backend.domain.building.domain.Building;
import ku_rum.backend.domain.building.domain.repository.BuildingRepository;
import ku_rum.backend.domain.department.domain.Department;
import ku_rum.backend.domain.department.domain.repository.DepartmentRepository;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.domain.user.dto.request.mail.EmailValidationRequest;
import ku_rum.backend.domain.user.dto.request.UserSaveRequest;
import ku_rum.backend.domain.user.dto.response.UserSaveResponse;
import ku_rum.backend.global.exception.user.DuplicateEmailException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    private Building building;

    private Department department;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
         building = Building.of("신공학관",3L, "신공", BigDecimal.valueOf(64.3423423), BigDecimal.valueOf(64.3423423));
         buildingRepository.save(building);

         department = Department.of("컴퓨터공학부", building);
         departmentRepository.save(department);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        buildingRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원을 올바르게 저장을 요청하면 저장한다.")
    void saveMember() {
        //given
        UserSaveRequest request = UserSaveRequest.builder()
                .email("kmw106933")
                .password("password123")
                .nickname("미미미누")
                .studentId("202112322")
                .department("컴퓨터공학부")
                .build();
        //when
        UserSaveResponse userSaveResponse = userService.saveUser(request);

        //then
        assertThat(userSaveResponse.getId()).isNotNull();
    }

    @Test
    @DisplayName("회원의 아이디가 이미 있는 경우 예외를 처리한다.")
    void validateEmail() {
        //given
        User user = User.builder()
                .email("kmw106933")
                .nickname("미미미누")
                .password("password123")
                .studentId("202112322")
                .department(department)
                .build();

        userRepository.save(user);

        EmailValidationRequest emailValidationRequest = new EmailValidationRequest("kmw106933");

        //when then
        assertThatThrownBy(() -> userService.validateEmail(emailValidationRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

}