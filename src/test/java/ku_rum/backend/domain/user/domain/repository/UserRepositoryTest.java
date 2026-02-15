package ku_rum.backend.domain.user.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import ku_rum.backend.domain.college.domain.College;
import ku_rum.backend.domain.oauth.domain.ProviderType;
import ku_rum.backend.domain.department.domain.Department;
import ku_rum.backend.domain.department.domain.repository.DepartmentRepository;
import ku_rum.backend.domain.friend.application.FriendReportService;
import ku_rum.backend.domain.friend.domain.repository.FriendBlockRepository;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.global.domain.repository.ApiLogRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserRepositoryTest {

    private User user;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EntityManager entityManager;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private ApiLogRepository apiLogRepository;

    @MockBean
    private FriendReportService friendManageService;

    @MockBean
    private FriendBlockRepository friendBlockRepository;

    @BeforeEach
    void setup() {
        College college = College.of("공과대학");
        Department department = Department.of("컴퓨터공학부", college, "url");
        departmentRepository.save(department);

        user = User.builder()
                .loginId("kmw106933")
                .email("kmw106933@naver.com")
                .nickname("미미미누")
                .password("password123")
                .studentId("202112322")
                .build();
    }

    @Test
    @DisplayName("User를 저장한다.")
    void save() {
        // given when
        User savedUser = userRepository.save(user);

        //then
        assertNotNull(savedUser);
        Assertions.assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    @DisplayName("findByOauthId로 조회 시 roles가 함께 로드되어 LazyInitializationException이 발생하지 않는다")
    void findByOauthId_withEntityGraph_loadsRolesEagerly() {
        // given
        User oauthUser = User.builder()
                .oauthId("google_12345")
                .nickname("OAuth유저")
                .providerType(ProviderType.GOOGLE)
                .build();
        userRepository.save(oauthUser);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<User> foundUser = userRepository.findByOauthId("google_12345");

        // then
        assertThat(foundUser).isPresent();
        assertThatCode(() -> foundUser.get().getRoles().size())
                .doesNotThrowAnyException();
    }

}