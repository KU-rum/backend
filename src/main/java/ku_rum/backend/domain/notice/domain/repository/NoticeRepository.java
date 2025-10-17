package ku_rum.backend.domain.notice.domain.repository;

import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.notice.domain.PublishStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Notice> findByCategoryIdAndPublishStatus(Long categoryId, PublishStatus publishStatus, Pageable pageable);
}
