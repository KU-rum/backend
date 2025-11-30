package ku_rum.backend.domain.alarm.domain.repository;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.alarm.domain.Alarm;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("""
            SELECT a FROM Alarm a
            WHERE a.user = :user
              AND (:lastId IS NULL OR a.id < :lastId)
            ORDER BY a.createdAt desc 
            """)
    List<Alarm> findAlarms(@Param("user") User user, @Param("lastId") Long lastId, Pageable pageable);

    Optional<Alarm> findById(Long AlarmId);
}
