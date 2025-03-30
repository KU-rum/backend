package ku_rum.backend.domain.notice.domain.repository;

import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.NoticeCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, String>, NoticeRepositoryCustom {
    //List<Notice> findByNoticeCategory(NoticeCategory noticeCategory);

    @Query("SELECT n FROM Notice n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Notice> searchNoticesByTitle(@Param("searchTerm") String searchTerm);

    //페이징 처리
    List<Notice> findByNoticeCategoryOrderByDateDesc(NoticeCategory noticeCategory, Pageable pageable);

    Optional<Notice> findByUrl(String link);

    boolean existsByUrl(String url);
}
