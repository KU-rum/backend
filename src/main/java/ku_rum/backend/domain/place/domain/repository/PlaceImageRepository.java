package ku_rum.backend.domain.place.domain.repository;

import java.util.List;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {
    @Query("SELECT pi FROM PlaceImage pi WHERE pi.place IN :places")
    List<PlaceImage> findByPlaceIn(@Param("places") List<Place> places);

    List<PlaceImage> findByPlace(Place place);

    void deleteByPlace(Place place);
}
