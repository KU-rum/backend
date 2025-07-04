package ku_rum.backend.domain.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotNull.List;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    @NotNull
    private String name;

    private String subName;

    @NotNull
    private String content;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 9)
    private BigDecimal latitude;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 9)
    private BigDecimal longitude;
}
