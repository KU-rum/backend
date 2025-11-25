package ku_rum.backend.domain.notice.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.NoticeDetail;
import ku_rum.backend.domain.notice.domain.PublishStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeDetailRepository extends JpaRepository<NoticeDetail, Long> {

    Optional<NoticeDetail> findByNotice(Notice notice);

    @Query("SELECT n FROM Notice n " +
            "WHERE n.publishStatus = :status " +
            "AND n.pubDate >= :sinceTime")
    List<Notice> findByPublishStatusAndPubDateAfter(
            @Param("status") PublishStatus status,
            @Param("sinceTime") LocalDateTime sinceTime
    );
}
