package ku_rum.backend.domain.place.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.place.domain.CategoryChip;
import ku_rum.backend.domain.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("""
                SELECT p
                FROM Place p
                WHERE p.categoryChip = "BUILDING"
                ORDER BY
                    ABS(p.latitude - :latitude) + ABS(p.longitude - :longitude)
                LIMIT 1
            """)
    Place findNearestPlace(@Param("latitude") BigDecimal latitude, @Param("longitude") BigDecimal longitude);

    Optional<Place> findOneByName(String name);

    Optional<Place> findByPlaceId(Long placeId);

    List<Place> findByCategoryChip(CategoryChip categoryChip);

    List<Place> findByNameContaining(String query);
}
