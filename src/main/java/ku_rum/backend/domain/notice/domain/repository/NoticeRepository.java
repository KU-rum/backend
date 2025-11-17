package ku_rum.backend.domain.notice.domain.repository;

import java.util.List;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.PublishStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Notice> findByCategoryIdAndPublishStatus(Long categoryId, PublishStatus publishStatus, Pageable pageable);

    @Query(value = """
            SELECT n.*
            FROM notice n
            LEFT JOIN notice_bookmark nb ON nb.notice_id = n.id
            WHERE n.publish_status = 'SUCCESS_CRAWLING'
            GROUP BY n.id
            ORDER BY COUNT(nb.id) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Notice> findTopByBookmark(@Param("limit") Long limit);
}
