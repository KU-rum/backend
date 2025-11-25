package ku_rum.backend.domain.notice.domain.repository;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.notice.domain.SearchKeyword;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {

    List<SearchKeyword> findByUser(User user);

    Optional<SearchKeyword> findByUserAndKeyword(User user, String keyword);
}
