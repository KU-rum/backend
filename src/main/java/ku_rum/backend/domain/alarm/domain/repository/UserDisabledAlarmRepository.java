package ku_rum.backend.domain.alarm.domain.repository;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.alarm.domain.AlarmType;
import ku_rum.backend.domain.alarm.domain.UserDisabledAlarm;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDisabledAlarmRepository extends JpaRepository<UserDisabledAlarm, Long> {

    Optional<UserDisabledAlarm> findByUserAndAlarmType(User user, AlarmType alarmType);

    List<UserDisabledAlarm> findByUser(User user);
}
