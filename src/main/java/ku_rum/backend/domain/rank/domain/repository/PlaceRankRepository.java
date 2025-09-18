package ku_rum.backend.domain.rank.domain.repository;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.rank.domain.PlaceRank;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlaceRankRepository extends JpaRepository<PlaceRank, Long> {

    @Query(value = """
            SELECT * FROM place_rank pr
            WHERE pr.user_id = :userId
              AND pr.count >= (
                  SELECT MIN(sub.count) FROM (
                      SELECT DISTINCT pr2.count
                      FROM place_rank pr2
                      WHERE pr2.user_id = :userId
                      ORDER BY pr2.count DESC
                      LIMIT 3
                  ) AS sub
              )
            ORDER BY pr.count DESC
            """, nativeQuery = true)
    List<PlaceRank> findTop3RanksWithTiesByUser(@Param("userId") Long userId);

    Optional<PlaceRank> findByUserAndPlace(User user, Place place);

    @Modifying
    @Transactional
    @Query(value = """
                UPDATE place_rank pr
                JOIN place pl ON pr.place_place_id = pl.place_id
                JOIN position po ON po.place_place_id = pl.place_id
                SET pr.count = pr.count + 1
                WHERE po.position_id IN (:positionIds)
            """, nativeQuery = true)
    void updatePlaceRankByPositions(@Param("positionIds") List<Long> positionIds);
}