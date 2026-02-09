package ku_rum.backend.domain.banner.domain.repository;

import ku_rum.backend.domain.banner.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("""
        SELECT b
        FROM Banner b
        WHERE b.active = true
          AND (b.startAt IS NULL OR b.startAt <= :now)
          AND (b.endAt IS NULL OR b.endAt >= :now)
        ORDER BY b.displayOrder ASC
    """)
    List<Banner> findActiveBanners(LocalDateTime now);

    long countByActiveTrue();

    List<Banner> findByActiveTrueAndDisplayOrderGreaterThanEqual(int displayOrder);
}
