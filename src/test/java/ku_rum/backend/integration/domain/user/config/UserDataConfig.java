package ku_rum.backend.integration.domain.user.config;

import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.integration.domain.user.data.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class UserDataConfig {

    @Autowired
    UserRepository userRepository;

    @Bean
    public UserData userData() {
        return new UserData(userRepository);
    }
}
