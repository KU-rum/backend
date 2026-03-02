package ku_rum.backend.integration.user.data;

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
    public CustomUserDetails saveUserData() {
        User user = User.builder()
                .loginId("testId")
                .email("test@naver.com")
                .nickname("테스트닉네임")
                .password("testPassword")
                .studentId("1")
                .imageUrl("test.jpg")
                .build();
        userRepository.save(user);

        CustomUserDetails userDetails = CustomUserDetails.from(user);
        return userDetails;
    }

    @Transactional
    public void deleteUsers() {
        userRepository.deleteAll();
    }
}
