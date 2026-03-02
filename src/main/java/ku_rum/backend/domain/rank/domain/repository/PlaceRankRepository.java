package ku_rum.backend.domain.rank.domain.repository;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.rank.domain.PlaceRank;
import ku_rum.backend.domain.rank.dto.PlaceRankWithRankingProjection;
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

    @Query(value = """
                SELECT 
                    ranked.rank_id          AS rankId,
                    ranked.count            AS count,
                    u.nickname              AS nickname,
                    ranked.place_place_id   AS placePlaceId,
                    ranked.created_at       AS createdAt,
                    ranked.modified_at      AS modifiedAt,
                    ranked.ranking          AS ranking
                FROM (
                    SELECT 
                        pr.*, 
                        DENSE_RANK() OVER (ORDER BY pr.count DESC) AS ranking
                    FROM place_rank pr
                    WHERE place_place_id =:placeId
                ) ranked
                JOIN users u
                            ON u.id = ranked.user_id
                WHERE ranking <= 3 OR ranked.user_id = :userId
                ORDER BY ranking
            """, nativeQuery = true)
    List<PlaceRankWithRankingProjection> findTop3RanksWithTies(@Param("userId") Long userId,
                                                               @Param("placeId") Long placeId);

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

    @Query(value = """
                SELECT 
                    ranked.rank_id          AS rankId,
                    ranked.count            AS count,
                    u.nickname              AS nickname,
                    ranked.place_place_id   AS placePlaceId,
                    ranked.created_at       AS createdAt,
                    ranked.modified_at      AS modifiedAt,
                    ranked.ranking          AS ranking
                FROM (
                    SELECT 
                        pr.*, 
                        DENSE_RANK() OVER (ORDER BY pr.count DESC) AS ranking
                    FROM place_rank pr
                    WHERE place_place_id =:placeId
                ) ranked
                JOIN users u
                            ON u.id = ranked.user_id
                WHERE ranking >= :startRank AND ranking <= :endRank
                ORDER BY ranking
            """, nativeQuery = true)
    List<PlaceRankWithRankingProjection> findRankByRange(@Param("placeId") Long placeId,
                                                         @Param("startRank") int startRank,
                                                         @Param("endRank") int endRank);

    @Query(value = """
                SELECT 
                    ranked.rank_id          AS rankId,
                    ranked.count            AS count,
                    u.nickname              AS nickname,
                    ranked.place_place_id   AS placePlaceId,
                    ranked.created_at       AS createdAt,
                    ranked.modified_at      AS modifiedAt,
                    ranked.ranking          AS ranking
                FROM (
                    SELECT 
                        pr.*, 
                        DENSE_RANK() OVER (ORDER BY pr.count DESC) AS ranking
                    FROM place_rank pr
                    WHERE place_place_id =:placeId
                ) ranked
                JOIN users u
                    ON u.id = ranked.user_id
                WHERE (ranked.ranking > :lastRank)
                   OR (ranked.ranking = :lastRank AND ranked.rank_id > :lastRankId)
                ORDER BY ranked.ranking, ranked.rank_id
                LIMIT :limit
            """, nativeQuery = true)
    List<PlaceRankWithRankingProjection> findRankByRange(@Param("placeId") Long placeId,
                                                         @Param("lastRank") int lastRank,
                                                         @Param("lastRankId") Long lastRankId,
                                                         @Param("limit") int limit);

    @Query(value = """
                SELECT 
                    ranked.rank_id          AS rankId,
                    ranked.count            AS count,
                    u.nickname              AS nickname,
                    ranked.place_place_id   AS placePlaceId,
                    ranked.created_at       AS createdAt,
                    ranked.modified_at      AS modifiedAt,
                    ranked.ranking          AS ranking
                FROM (
                    SELECT 
                        pr.*, 
                        DENSE_RANK() OVER (ORDER BY pr.count DESC) AS ranking
                    FROM place_rank pr
                    WHERE place_place_id = :placeId
                ) ranked
                JOIN users u
                    ON u.id = ranked.user_id
                WHERE ranked.user_id = :userId
                   AND ranked.place_place_id = :placeId
                LIMIT 1
            """, nativeQuery = true)
    Optional<PlaceRankWithRankingProjection> findRankByPlaceAndUser(@Param("placeId") Long placeId,
                                                                    @Param("userId") Long userId);

    @Query(
            value = """
                    SELECT ranking
                    FROM (
                        SELECT rank_id, RANK() OVER (ORDER BY count DESC) AS ranking
                        FROM place_rank
                    ) AS ranked
                    WHERE rank_id = :rankId
                    """,
            nativeQuery = true
    )
    Integer findRankingByRankId(@Param("rankId") Long rankId);
}