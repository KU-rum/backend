package ku_rum.backend.integration.domain.user.data;

import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.security.CustomUserDetails;
import org.springframework.transaction.annotation.Transactional;

public class UserData {

    UserRepository userRepository;

    public UserData(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public CustomUserDetails saveUser() {
        User user = User.builder()
                .loginId("testId")
                .nickname("테스트유저")
                .password("password")
                .build();
        userRepository.save(user);

        return CustomUserDetails.from(user);
    }

    @Transactional
    public void afterEach() {
        userRepository.deleteAllHard();
    }
}
