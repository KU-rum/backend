package ku_rum.backend.domain.bookmark.domain.repository;

import java.util.List;
import ku_rum.backend.domain.bookmark.domain.NoticeBookmark;
import ku_rum.backend.domain.notice.domain.Notice;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<NoticeBookmark, Long> {

    boolean existsByUserAndNotice(User user, Notice notice);

    List<NoticeBookmark> findByUser(User user);

    void deleteById(Long bookmarkId);
}
