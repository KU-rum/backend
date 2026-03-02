package ku_rum.backend.domain.place.domain.repository;

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

    @Query(value = """
            SELECT * FROM place
            WHERE ST_Contains(boundary, ST_GeomFromText(:point, 4326))
            LIMIT 1
            """, nativeQuery = true)
    Optional<Place> findContainingPoint(@Param("point") String point);

    Optional<Place> findOneByName(String name);

    Optional<Place> findByPlaceId(Long placeId);

    List<Place> findByCategoryChip(CategoryChip categoryChip);

    List<Place> findByNameContaining(String query);
}
