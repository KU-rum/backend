package ku_rum.backend.domain.place.domain.repository;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.PlaceHistory;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceHistoryRepository extends JpaRepository<PlaceHistory, Long> {

    List<PlaceHistory> findTop5ByUserOrderByModifiedAtDesc(User user);

    Optional<PlaceHistory> findByPlaceAndUser(Place place, User user);
}
