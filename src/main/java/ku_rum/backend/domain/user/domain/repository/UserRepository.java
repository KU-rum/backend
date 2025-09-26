package ku_rum.backend.domain.user.domain.repository;

import java.util.List;
import java.util.Optional;
import ku_rum.backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByStudentId(String studentId);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findUserById(Long id);

    Optional<User> findUserByNickname(String nickname);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByLoginId(String loginId);

    Optional<User> findByOauthId(String oauthId);

    List<User> findByNicknameContainingIgnoreCase(String nickname);

    @Modifying
    @Query("DELETE FROM User")
    void deleteAllHard();
}
