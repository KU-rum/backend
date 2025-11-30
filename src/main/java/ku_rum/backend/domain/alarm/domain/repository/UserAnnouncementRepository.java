package ku_rum.backend.domain.alarm.domain.repository;

import java.util.List;
import ku_rum.backend.domain.alarm.domain.UserAnnouncement;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnnouncementRepository extends JpaRepository<UserAnnouncement, Long> {

    @Query("""
            SELECT ua
            FROM UserAnnouncement ua
            JOIN ua.announcement a
            WHERE ua.user = :user
              AND (:lastId IS NULL OR ua.id < :lastId)
            ORDER BY ua.createdAt DESC
            """)
    List<UserAnnouncement> findUserAnnouncement(@Param("user") User user, @Param("lastId") Long lastId,
                                                Pageable pageable);
}
