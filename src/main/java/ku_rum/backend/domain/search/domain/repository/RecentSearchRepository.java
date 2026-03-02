package ku_rum.backend.domain.search.domain.repository;

import ku_rum.backend.domain.search.domain.RecentSearch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {

    Optional<RecentSearch> findByUserIdAndKeyword(Long userId, String keyword);

    List<RecentSearch> findByUserIdOrderByUpdatedAtDesc(Long userId, Pageable pageable);

    long deleteByIdAndUserId(Long id, Long userId);

    long deleteByUserId(Long userId);
}
