package ku_rum.backend.domain.notice.domain.repository;

import java.time.LocalDateTime;
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

    // 키워드 검색 (제목/본문에서 검색)
    @Query("""
            select n
            from Notice n
            where n.publishStatus = :status
              and (
                    lower(n.title)       like lower(concat('%', :keyword, '%'))
                 or lower(n.description) like lower(concat('%', :keyword, '%'))
              )
            """)
    Page<Notice> searchByKeyword(@Param("keyword") String keyword,
                                 @Param("status") PublishStatus status,
                                 Pageable pageable);

    Page<Notice> findByIsImportantTrueAndPublishStatus(PublishStatus publishStatus,
                                                       Pageable pageable);

    @Query("SELECT n FROM Notice n " +
            "WHERE n.publishStatus = :status " +
            "AND n.pubDate >= :sinceTime")
    List<Notice> findByPublishStatusAndPubDateAfter(
            @Param("status") PublishStatus status,
            @Param("sinceTime") LocalDateTime sinceTime
    );
}
