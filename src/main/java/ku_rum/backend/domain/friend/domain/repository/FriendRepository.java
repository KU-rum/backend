package ku_rum.backend.domain.friend.domain.repository;

import ku_rum.backend.domain.friend.domain.Friend;
import ku_rum.backend.domain.friend.domain.vo.FriendStatus;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    void deleteByFromUserAndToUser(User fromUser, User toUser);

    Optional<Friend> findByFromUserAndToUser(User fromUser, User toUser);

    List<Friend> findByToUserAndStatus(User toUser, FriendStatus status);

    List<Friend> findByFromUserAndStatus(User fromUser, FriendStatus status);

    @Query("SELECT f.toUser.id FROM Friend f " +
            "WHERE f.fromUser.id = :fromUserId AND f.toUser.id IN :targetUserIds AND f.status = :status")
    List<Long> findToUserIdsByFromUserAndStatus(@Param("fromUserId") Long fromUserId,
                                                @Param("targetUserIds") List<Long> targetUserIds,
                                                @Param("status") FriendStatus status);

    @Query("SELECT f.fromUser.id FROM Friend f " +
            "WHERE f.toUser.id = :toUserId AND f.fromUser.id IN :targetUserIds AND f.status = :status")
    List<Long> findFromUserIdsByToUserAndStatus(@Param("toUserId") Long toUserId,
                                                @Param("targetUserIds") List<Long> targetUserIds,
                                                @Param("status") FriendStatus status);

    @Query("SELECT f.toUser.id FROM Friend f " +
            "WHERE f.fromUser.id = :currentUserId AND f.toUser.id IN :targetUserIds AND f.status = :status " +
            "UNION " +
            "SELECT f.fromUser.id FROM Friend f " +
            "WHERE f.toUser.id = :currentUserId AND f.fromUser.id IN :targetUserIds AND f.status = :status")
    List<Long> findFriendUserIds(@Param("currentUserId") Long currentUserId,
                                 @Param("targetUserIds") List<Long> targetUserIds,
                                 @Param("status") FriendStatus status);

    boolean existsByFromUserAndToUserAndStatus(User fromUser, User toUser, FriendStatus status);

    Optional<Friend> findByFromUserAndToUserAndStatus(User fromUser, User toUser, FriendStatus status);
}