package ku_rum.backend.domain.banner.domain.repository;

import java.util.List;
import ku_rum.backend.domain.banner.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findAllByOrderByCreatedAtDesc();
}
