package ku_rum.backend.domain.alarm.domain.repository;

import ku_rum.backend.domain.alarm.domain.UserAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnnouncementRepository extends JpaRepository<UserAnnouncement, Long> {
}
