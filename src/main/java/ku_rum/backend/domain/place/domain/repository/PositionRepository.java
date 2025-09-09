package ku_rum.backend.domain.place.domain.repository;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.place.domain.Place;
import ku_rum.backend.domain.place.domain.Position;
import ku_rum.backend.domain.place.dto.FriendUserDto;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    boolean existsPositionByUser(User user);

    Optional<Position> findPositionByUser(User user);

    @Transactional
    void deleteByUser(User user);

    @Query("""
                SELECT new ku_rum.backend.domain.place.dto.FriendUserDto(
                    user.id,
                    user.nickname,
                    user.imageUrl,
                    p.place
                )
                FROM Friend f
                JOIN User user
                    ON user.id = CASE 
                    WHEN f.fromUser.id = :userId THEN f.toUser.id
                    ELSE f.fromUser.id
                    END
                JOIN Position p 
                    ON p.user.id = user.id
                WHERE f.status = 'ACCEPT'
                  AND (f.fromUser.id = :userId OR f.toUser.id = :userId)
            """)
    List<FriendUserDto> findPlaceByFriend(@Param("userId") Long userId);

    @Query("""
                SELECT new ku_rum.backend.domain.place.dto.FriendUserDto(
                    user.id,
                    user.nickname,
                    user.imageUrl,
                    p.place
                )
                FROM Friend f
                JOIN User user
                    ON user.id = CASE
                    WHEN f.fromUser.id = :userId THEN f.toUser.id
                    ELSE f.fromUser.id
                    END
                JOIN Position p
                    ON p.user.id = user.id
                WHERE f.status = 'ACCEPT'
                  AND (f.fromUser.id = :userId OR f.toUser.id = :userId)
                  AND p.place = :place
            """)
    List<FriendUserDto> findPositionByFriendAndPlace(@Param("userId") Long userId, @Param("place") Place place);
}
