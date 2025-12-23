package ku_rum.backend.domain.search.application;

import ku_rum.backend.domain.search.domain.RecentSearch;
import ku_rum.backend.domain.search.domain.repository.RecentSearchRepository;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentSearchService {

    private static final int MAX_RECENT_SEARCH = 20;

    private final RecentSearchRepository repository;
    private final UserService userService;

    @Transactional
    public void save(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim();
        if (normalized.isEmpty()) return;

        User user = userService.getUser();
        LocalDateTime now = LocalDateTime.now();

        repository.findByUserIdAndKeyword(user.getId(), normalized)
                .ifPresentOrElse(
                        rs -> rs.touch(now),
                        () -> repository.save(
                                new RecentSearch(user.getId(), normalized, now)
                        )
                );

        // 최대 개수 초과 시 오래된 것 삭제
        List<RecentSearch> list =
                repository.findByUserIdOrderByUpdatedAtDesc(
                        user.getId(),
                        PageRequest.of(0, MAX_RECENT_SEARCH + 1)
                );

        if (list.size() > MAX_RECENT_SEARCH) {
            repository.deleteAll(list.subList(MAX_RECENT_SEARCH, list.size()));
        }
    }

    @Transactional(readOnly = true)
    public List<RecentSearch> list(int limit) {
        User user = userService.getUser();
        return repository.findByUserIdOrderByUpdatedAtDesc(
                user.getId(),
                PageRequest.of(0, limit)
        );
    }

    @Transactional
    public void delete(Long id) {
        User user = userService.getUser();
        repository.deleteByIdAndUserId(id, user.getId());
    }

    @Transactional
    public void deleteAll() {
        User user = userService.getUser();
        repository.deleteByUserId(user.getId());
    }
}
