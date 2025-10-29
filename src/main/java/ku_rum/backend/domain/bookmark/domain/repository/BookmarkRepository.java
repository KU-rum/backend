package ku_rum.backend.domain.bookmark.domain.repository;

import ku_rum.backend.domain.bookmark.domain.NoticeBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<NoticeBookmark, Long> {
}
