package ku_rum.backend.domain.alarm.domain.repository;

import ku_rum.backend.domain.alarm.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}
