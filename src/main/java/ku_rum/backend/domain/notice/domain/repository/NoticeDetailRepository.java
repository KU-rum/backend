package ku_rum.backend.domain.notice.domain.repository;

import java.util.Optional;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.NoticeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeDetailRepository extends JpaRepository<NoticeDetail, Long> {

    Optional<NoticeDetail> findByNotice(Notice notice);
}
