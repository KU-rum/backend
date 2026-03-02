package ku_rum.backend.domain.search.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "recent_search",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_recent_search_user_keyword",
                columnNames = {"user_id", "keyword"}
        ),
        indexes = @Index(
                name = "idx_recent_search_user_updated",
                columnList = "user_id, updated_at"
        )
)
public class RecentSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String keyword;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public RecentSearch(Long userId, String keyword, LocalDateTime now) {
        this.userId = userId;
        this.keyword = keyword;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void touch(LocalDateTime now) {
        this.updatedAt = now;
    }
}
