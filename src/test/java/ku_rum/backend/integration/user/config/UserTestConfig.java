package ku_rum.backend.integration.user.config;

import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.integration.user.data.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class UserTestConfig {

    @Autowired
    UserRepository userRepository;

    @Bean
    public UserData userData() {
        return new UserData(userRepository);
    }
}
