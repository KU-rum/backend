package ku_rum.backend.domain.search.application;

import ku_rum.backend.domain.search.domain.RecentSearch;
import ku_rum.backend.domain.search.domain.repository.RecentSearchRepository;
import ku_rum.backend.domain.user.application.UserService;
import ku_rum.backend.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        String normalized = normalizeKeyword(keyword);
        if (normalized == null) {
            return;
        }

        User user = userService.getUser();
        LocalDateTime now = LocalDateTime.now();

        try {
            repository.findByUserIdAndKeyword(user.getId(), normalized)
                    .ifPresentOrElse(
                            rs -> rs.touch(now),
                            () -> repository.save(
                                    new RecentSearch(user.getId(), normalized, now)
                            )
                    );
        } catch (DataIntegrityViolationException e) {
            repository.findByUserIdAndKeyword(user.getId(), normalized)
                    .ifPresent(rs -> rs.touch(now));
        }

        deleteRecentKeywordsOversize(user);
    }

    private void deleteRecentKeywordsOversize(User user) {
        List<RecentSearch> list =
                repository.findByUserIdOrderByUpdatedAtDesc(
                        user.getId(),
                        PageRequest.of(0, MAX_RECENT_SEARCH + 1)
                );

        if (list.size() > MAX_RECENT_SEARCH) {
            repository.deleteAll(list.subList(MAX_RECENT_SEARCH, list.size()));
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }

        String trimmed = keyword.trim();
        if (trimmed.isEmpty()) return null;
        return trimmed;
    }

    @Transactional(readOnly = true)
    public List<RecentSearch> list(final int limit) {
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
