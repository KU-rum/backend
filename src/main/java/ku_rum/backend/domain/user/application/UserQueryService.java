package ku_rum.backend.domain.user.application;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_USER;

import java.util.List;
import ku_rum.backend.domain.user.domain.User;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.exception.user.NoSuchUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {
    private final UserRepository userRepository;

    public User getUserByEmail(final String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NoSuchUserException(NO_SUCH_USER));
    }

    public User getUserByLoginId(final String loginId) {
        return userRepository.findUserByLoginId(loginId)
                .orElseThrow(() -> new NoSuchUserException(NO_SUCH_USER));
    }

    public User getUserById(final Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new GlobalException(NO_SUCH_USER));
    }

    public List<User> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }
}
