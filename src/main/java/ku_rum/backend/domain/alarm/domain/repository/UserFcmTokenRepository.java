package ku_rum.backend.domain.alarm.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {
    Optional<UserFcmToken> findByUser(User user);

    List<UserFcmToken> findByUserIn(Collection<User> users);
}
